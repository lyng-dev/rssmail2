package com.rssmail;

import java.util.LinkedList;
import java.util.Queue;

import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.ApplicationContextJobFactory;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.scheduler.SubscriptionUpdateConsumer;
import com.rssmail.services.EmailService.AwsSesEmailService;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.services.SubscriptionService.SubscriptionService;
import com.rssmail.utils.UUID;
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
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

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
  public String getEnvAwsAccessKeyId() {
    final String localEnv = "RSSMAIL_AWS_ACCESS_KEY_ID";
    final String env_var = System.getenv(localEnv);
    if (env_var != null && System.getenv(localEnv).length() > 0) {
      System.out.println(String.format("FOUND %s", localEnv));
      return System.getenv(localEnv);
    } else {
      return "";
    }
  };

  public String getEnvAwsSecretAccessKey() {
    final String localEnv = "RSSMAIL_AWS_SECRET_ACCESS_KEY";
    final String env_var = System.getenv(localEnv);
    if (env_var != null && System.getenv(localEnv).length() > 0) {
      System.out.println(String.format("FOUND %s", localEnv));
      return System.getenv(localEnv);
    } else {
      return "";
    }
  };

  public String getEnvAwsRegion() {
    final String localEnv = "RSSMAIL_AWS_REGION";
    final String env_var = System.getenv(localEnv);
    if (env_var != null && System.getenv(localEnv).length() > 0) {
      System.out.println(String.format("FOUND %s", localEnv));
      return System.getenv(localEnv);
    } else {
      return EC2MetadataUtils.getEC2InstanceRegion();
    }
  };

  @Value("${senderemail:rssmail@lyng.dev}")
  public String senderEmail;

  //fields: application properties
  @Value("${aws.dynamodb.subscriptions-table-name:rssmail-subscriptions}")
  public String awsDynamoDbSubscriptionsTableName;

  //Beans
  private AwsBasicCredentials getAwsCredentials() {
    return AwsBasicCredentials.create(getEnvAwsAccessKeyId(), getEnvAwsSecretAccessKey());
  }

  @Bean AwsCredentialsProvider getCredentialsProvider() {
    try {
      if (EC2MetadataUtils.getAmiId().length() > 0) { 
        //obtain instance credentials
        var credentialsProvider = InstanceProfileCredentialsProvider.create();
        if (credentialsProvider.resolveCredentials().accessKeyId().length() > 0 && credentialsProvider.resolveCredentials().secretAccessKey().length() > 0) {
          return credentialsProvider;
        } 
      }
    } catch (Exception e) {
      //swallow for now
    }
    //default to basic, for local
    return StaticCredentialsProvider.create(getAwsCredentials());
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
      return sesAsyncClientBuilder.credentialsProvider(getCredentialsProvider()).build();
  }

  @Bean
  Region awsRegion() {
    final String region = getEnvAwsRegion();
    return Region.of(region);
  }

  @Bean
  public UUID getUuid() {
    return new UUID();
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
      return dynamodbDbBuilder.credentialsProvider(getCredentialsProvider()).build();
  }

  @Bean
  public AwsSubscriptionService awsSubscriptionService(DynamoDbAsyncClient dynamoDbAsyncClient) {
    return new AwsSubscriptionService(dynamoDbAsyncClient, (EmailService)appContext.getBean(EmailService.class), (RssMailScheduler)appContext.getBean("rssMailScheduler"), (UUID)appContext.getBean("getUuid"), awsDynamoDbSubscriptionsTableName);
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