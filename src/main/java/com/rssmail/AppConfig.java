package com.rssmail;

import com.rssmail.services.AwsSubscriptionService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class AppConfig {

  final Environment env;

  public AppConfig(Environment env) {
    this.env = env;
  }

  @Bean
  Region awsRegion() {
    final String region = env.getProperty("AWS_REGION");
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