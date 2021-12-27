package com.rssmail.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Service
public class AwsSubscriptionService implements SubscriptionService {

  final DynamoDbAsyncClient db;
  private String subscriptionTableName;

  public AwsSubscriptionService(DynamoDbAsyncClient dynamoDb, String subscriptionTableName) {
    this.db = dynamoDb;
    this.subscriptionTableName = subscriptionTableName;
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
