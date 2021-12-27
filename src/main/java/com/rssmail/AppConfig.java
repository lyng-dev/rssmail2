package com.rssmail;

import com.rssmail.services.AwsSubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
public class AppConfig {

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

  //fields: application properties
  @Value("${aws.dynamodb.subscriptions-table-name:banana}")
  public String awsDynamoDbSubscriptionsTableName;

  //Beans
  @Bean AwsBasicCredentials getAwsCredentials() {
    return AwsBasicCredentials.create(envAwsAccessKeyId, envAwsSecretAccessKey);
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

//  @Bean
//  public DynamoDbClient dynamoDbClient(DynamoDbClientBuilder dynamoDbClientBuilder) {
//      return dynamoDbClientBuilder.credentialsProvider(StaticCredentialsProvider.create(getAwsCredentials())).build();
//  }

  @Bean
  public AwsSubscriptionService awsSubscriptionService(DynamoDbAsyncClient dynamoDbAsyncClient) {
    return new AwsSubscriptionService(dynamoDbAsyncClient, awsDynamoDbSubscriptionsTableName);
  }
}