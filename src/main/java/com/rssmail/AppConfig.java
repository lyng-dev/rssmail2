package com.rssmail;

import com.rssmail.services.AwsSubscriptionServiceImpl;
import com.rssmail.services.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
public class AppConfig {

    @Autowired Environment env;

    @Bean
    public DynamoDbAsyncClientBuilder buildDynamoDbClientBuilder() {
      return DynamoDbAsyncClient.builder().region(Region.EU_CENTRAL_1);
    }

    @Bean
    public DynamoDbAsyncClient buildDynamoDbClient(DynamoDbAsyncClientBuilder dynamodbDbBuilder) {
        return dynamodbDbBuilder.build();
    }
}