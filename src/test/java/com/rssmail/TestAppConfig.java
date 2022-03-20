package com.rssmail;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.utils.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@TestConfiguration
public class TestAppConfig {

  @Autowired private ApplicationContext appContext;

  @Autowired
  Environment env;

  @Value("${AWS_ACCESS_KEY_ID}")
  public String envAwsAccessKeyId;

  @Value("${AWS_SECRET_ACCESS_KEY}")
  public String envAwsSecretAccessKey;

  @Value("${AWS_REGION}")
  public String envAwsRegion;

  //fields: application properties
  @Value("${aws.dynamodb.subscriptions-table-name:test-table}")
  public String awsDynamoDbSubscriptionsTableName;

  @Bean
  @Primary
  public Region awsRegion() {
    return Region.of(envAwsRegion);
  }

  @Bean
  @Primary
  public DynamoDbAsyncClientBuilder dynamoDbClientBuilder() {
    return DynamoDbAsyncClient.builder().region(awsRegion());
  }

  @Bean
  @Primary
  public DynamoDbAsyncClient dynamoDbClient(DynamoDbAsyncClientBuilder dynamodbDbBuilder) {
      return dynamodbDbBuilder.build();
  }

  @Bean
  public AwsSubscriptionService awsSubscriptionService(DynamoDbAsyncClient dynamoDbAsyncClient) {
    return new AwsSubscriptionService(dynamoDbAsyncClient, (EmailService)appContext.getBean(EmailService.class), (RssMailScheduler)appContext.getBean("rssMailScheduler"), (UUID)appContext.getBean("getUuid"), awsDynamoDbSubscriptionsTableName);
  }
}