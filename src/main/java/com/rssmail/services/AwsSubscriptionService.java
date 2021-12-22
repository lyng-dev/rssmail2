package com.rssmail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Service
public class AwsSubscriptionService implements SubscriptionService {

  final DynamoDbAsyncClient db;

  @Autowired
  public AwsSubscriptionService(DynamoDbAsyncClient dynamoDb) {
    this.db = dynamoDb;
  }

  @Override
  public boolean createSubscription(String feedUrl, String recipient) {
    return true; 
  }

  @Override
  public boolean deleteSubscription(String tokenId, String recipient) {
    return true; 
  }
  
}
