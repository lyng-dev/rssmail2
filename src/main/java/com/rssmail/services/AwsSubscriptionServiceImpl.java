package com.rssmail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Service
public class AwsSubscriptionServiceImpl implements SubscriptionService {

  private DynamoDbAsyncClient db;
  
  @Autowired
  public AwsSubscriptionServiceImpl(DynamoDbAsyncClient dynamoDb) {
    this.db = dynamoDb;
  }

  @Override
  public void createSubscription(String feedUrl, String recipient) {
    
  }

  @Override
  public void deleteSubscription(String tokenId, String recipient) {
  }
  
}
