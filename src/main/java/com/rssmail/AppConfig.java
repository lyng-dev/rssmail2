package com.rssmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class AppConfig {

  //environment variables
  @Value("${AWS_REGION:us-east-1}")
  private String envAwsRegion;

  final Environment env;

  public AppConfig(Environment env) {
    this.env = env;
  }

  @Bean
  Region awsRegion() {
    final String region = envAwsRegion;
    return Region.of(region);
  }

  @Bean
  public DynamoDbAsyncClientBuilder dynamoDbClientBuilder() {
    return DynamoDbAsyncClient.builder().region(awsRegion());
  }

  @Bean
  public DynamoDbAsyncClient dynamoDbClient(DynamoDbAsyncClientBuilder dynamodbDbBuilder) {
      return dynamodbDbBuilder.build();
  }

}