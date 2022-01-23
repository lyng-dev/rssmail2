package com.rssmail;

import java.util.LinkedList;
import java.util.Queue;

import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.scheduler.SubscriptionUpdateConsumer;
import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.services.EmailService.AwsSesEmailService;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.services.SubscriptionService.SubscriptionService;
import com.rssmail.utils.hashing.HashTree;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.SesAsyncClientBuilder;

@Configuration
public class AppConfig {

  @Autowired private ApplicationContext appContext;
  
  //constructor
  public AppConfig(Environment env) {
    this.env = env;
  }

  //fields
  final Environment env;

  //fields: environment variables
  @Value("${RSSMAIL_AWS_ACCESS_KEY_ID}")
  public String envAwsAccessKeyId;

  @Value("${RSSMAIL_AWS_SECRET_ACCESS_KEY}")
  public String envAwsSecretAccessKey;

  @Value("${RSSMAIL_AWS_REGION}")
  public String envAwsRegion;

  @Value("${senderemail:rssmail@lyng.dev}")
  public String senderEmail;

  //fields: application properties
  @Value("${aws.dynamodb.subscriptions-table-name:rssmail-subscriptions}")
  public String awsDynamoDbSubscriptionsTableName;

  //Beans
  @Bean AwsBasicCredentials getAwsCredentials() {
    return AwsBasicCredentials.create(envAwsAccessKeyId, envAwsSecretAccessKey);
  }

  @Bean
  public SesAsyncClientBuilder sesAsyncClientBuilder() {
    return SesAsyncClient.builder().region(awsRegion());
  }

  @Bean
  public SesAsyncClientBuilder sesClientBuilder() {
    return SesAsyncClient.builder().region(awsRegion());
  }

  @Bean
  public SesAsyncClient sesAsyncClient(SesAsyncClientBuilder sesAsyncClientBuilder) {
      return sesAsyncClientBuilder.credentialsProvider(StaticCredentialsProvider.create(getAwsCredentials())).build();
  }

  @Bean
  Region awsRegion() {
    final String region = envAwsRegion;
    return Region.of(region);
  }

  @Bean
  public DynamoDbAsyncClientBuilder dynamoDbAsyncClientBuilder() {
    return DynamoDbAsyncClient.builder().region(awsRegion());
  }

  @Bean
  public DynamoDbClientBuilder dynamoDbClientBuilder() {
    return DynamoDbClient.builder().region(awsRegion());
  }
  
  @Bean
  public software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient dynamoDbAsyncClient(DynamoDbAsyncClientBuilder dynamodbDbBuilder) {
      return dynamodbDbBuilder.credentialsProvider(StaticCredentialsProvider.create(getAwsCredentials())).build();
  }

  @Bean
  public AwsSubscriptionService awsSubscriptionService(DynamoDbAsyncClient dynamoDbAsyncClient) {
    return new AwsSubscriptionService(dynamoDbAsyncClient, (EmailService)appContext.getBean(EmailService.class), (RssMailScheduler)appContext.getBean("rssMailScheduler"),  awsDynamoDbSubscriptionsTableName);
  }

  @Bean 
  public RssMailScheduler rssMailScheduler() throws SchedulerException {
    return new RssMailScheduler(
      new StdSchedulerFactory(), 
      appContext.getBean(ApplicationContextJobFactory.class), 
      appContext.getBean(HashTree.class), 
      (HandledSubscriptionFeedItemsContentStore)appContext.getBean("feedSubscriptionLastUpdatedContentStore"),
      (Queue<SubscriptionUpdate>)appContext.getBean("subscriptionUpdatesQueue"));
  }
  
  @Bean SubscriptionUpdateConsumer subscriptionUpdateConsumer() throws SchedulerException {
    return new SubscriptionUpdateConsumer(
      new StdSchedulerFactory(), 
      appContext.getBean(ApplicationContextJobFactory.class), 
      (HandledSubscriptionFeedItemsContentStore)appContext.getBean("feedSubscriptionLastUpdatedContentStore"),
      (Queue<SubscriptionUpdate>)appContext.getBean("subscriptionUpdatesQueue"),
      (SubscriptionService)appContext.getBean("awsSubscriptionService"),
      (EmailService)appContext.getBean(EmailService.class));
  }

  @Bean 
  public EmailService awsEmailService() {
    return new AwsSesEmailService(appContext.getBean(SesAsyncClient.class), senderEmail);
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public Queue<SubscriptionUpdate> subscriptionUpdatesQueue() {
    return new LinkedList<>();
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public HandledSubscriptionFeedItemsContentStore feedSubscriptionLastUpdatedContentStore() {
    return new HandledSubscriptionFeedItemsContentStore();
  }
}