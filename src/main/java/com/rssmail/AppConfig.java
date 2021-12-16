package com.rssmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class AppConfig {

  @Autowired 
  Environment env;

  @Bean Region awsRegion() {
    return Region.of(env.getProperty("AWS_REGION"));
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