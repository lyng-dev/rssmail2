package com.rssmail.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Service
public class AwsSubscriptionService implements SubscriptionService {

  final DynamoDbAsyncClient db;
  private String subscriptionTableName;

  public AwsSubscriptionService(DynamoDbAsyncClient dynamoDb, String subscriptionTableName) {
    this.db = dynamoDb;
    this.subscriptionTableName = subscriptionTableName;
  }

  @Override
  public String createSubscription(String feedUrl, String recipientEmail) {
    
    //generate subscriptionId
    var subscriptionId = UUID.randomUUID().toString();

    //generate default validationState
    var defaultValidationState = false;
    var validationCode = UUID.randomUUID().toString();

    //generate createdTime
    var created = Instant.now().toString();
    
    //prepare new subscription
    var itemValues = new HashMap<String, AttributeValue>();
    itemValues.put("subscriptionId", AttributeValue.builder().s(subscriptionId).build());
    itemValues.put("feedUrl", AttributeValue.builder().s(feedUrl).build());
    itemValues.put("recipientEmail", AttributeValue.builder().s(recipientEmail).build());
    itemValues.put("isValidated", AttributeValue.builder().bool(defaultValidationState).build());
    itemValues.put("validationCode", AttributeValue.builder().s(validationCode).build());
    itemValues.put("createdDate", AttributeValue.builder().s(created).build());

    //prepare request
    PutItemRequest request = PutItemRequest.builder()
      .tableName(subscriptionTableName)
      .item(itemValues)
      .build();

    //execute request
    var putItemFuture = db.putItem(request);
    var response = putItemFuture.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
      System.out.println("Created: " + subscriptionId);
      return subscriptionId;
    }
    else 
      return "";
  }

  @Override
  public Boolean deleteSubscription(String subscriptionId, String recipientEmail) {
   
    //prepare new subscription
    var deleteItemValues = new HashMap<String, AttributeValue>();
    deleteItemValues.put("subscriptionId", AttributeValue.builder().s(subscriptionId).build());

    //prepare request
    DeleteItemRequest request = DeleteItemRequest.builder()
      .tableName(subscriptionTableName)
      .key(deleteItemValues)
      .build();

    //execute request
    var deleteItemFuture = db.deleteItem(request);
    var response = deleteItemFuture.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
      System.out.println("Deleted: " + subscriptionId);
      return true;
    }
    else 
      return false;
  }

  @Override
  public Boolean validateSubscription(String subscriptionId, String validationCode) {
    
    //item to update
    var itemKey = new HashMap<String,AttributeValue>();
    itemKey.put("subscriptionId", AttributeValue.builder().s(subscriptionId).build());

    //values to update in item
    var updateItemValues = new HashMap<String, AttributeValueUpdate>();
    updateItemValues.put("isValidated", AttributeValueUpdate.builder().value(AttributeValue.builder().bool(true).build()).action(AttributeAction.PUT).build());

    //prepare request
    UpdateItemRequest request = UpdateItemRequest.builder()
      .tableName(subscriptionTableName)
      .key(itemKey)
      .attributeUpdates(updateItemValues)
      .build();

    //execute request
    var updateItemFuture = db.updateItem(request);
    var response = updateItemFuture.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
      System.out.println("Validated: " + subscriptionId + ", with ValicationCode: " + validationCode);
      return true;
    }
    else 
      return false;
  }

}
