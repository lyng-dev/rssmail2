package com.rssmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@TestConfiguration
public class TestAppConfig {

  @Autowired 
  Environment env;

  @Bean
  public Region awsRegion() {
    return Region.of("eu-central-1");
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