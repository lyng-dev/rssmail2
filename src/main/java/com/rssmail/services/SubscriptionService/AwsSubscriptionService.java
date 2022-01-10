package com.rssmail.services.SubscriptionService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.rssmail.models.Subscription;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Service
public class AwsSubscriptionService implements SubscriptionService {

  final private DynamoDbAsyncClient db;
  final private String subscriptionTableName;

  public AwsSubscriptionService(DynamoDbAsyncClient dynamoDb, String subscriptionTableName) {
    this.db = dynamoDb;
    this.subscriptionTableName = subscriptionTableName;
  }

  @Override
  public String createSubscription(String feedUrl, String recipientEmail) {
    //generate subscriptionId
    final var subscriptionId = UUID.randomUUID().toString();

    //generate default validationState
    final var defaultValidationState = false;
    final var validationCode = UUID.randomUUID().toString();

    //generate createdTime
    final var created = Instant.now().toString();
    
    //prepare new subscription
    final var itemValues = new HashMap<String, AttributeValue>();
    itemValues.put("subscriptionId", AttributeValue.builder().s(subscriptionId).build());
    itemValues.put("feedUrl", AttributeValue.builder().s(feedUrl).build());
    itemValues.put("recipientEmail", AttributeValue.builder().s(recipientEmail).build());
    itemValues.put("isValidated", AttributeValue.builder().bool(defaultValidationState).build());
    itemValues.put("validationCode", AttributeValue.builder().s(validationCode).build());
    itemValues.put("createdDate", AttributeValue.builder().s(created).build());

    //prepare request
    final var request = PutItemRequest.builder()
      .tableName(subscriptionTableName)
      .item(itemValues)
      .build();

    //execute request
    final var future = db.putItem(request);
    final var response = future.join();

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
   
    //item to delete
    final var itemKey = generateSubscriptionItemKey(subscriptionId);

    //prepare request
    final var request = DeleteItemRequest.builder()
      .tableName(subscriptionTableName)
      .key(itemKey)
      .build();

    //execute request
    final var future = db.deleteItem(request);
    final var response = future.join();

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
    final var itemKey = generateSubscriptionItemKey(subscriptionId);

    //values to update in item
    final var itemValues = new HashMap<String, AttributeValueUpdate>();
    itemValues.put("isValidated", AttributeValueUpdate.builder().value(AttributeValue.builder().bool(true).build()).action(AttributeAction.PUT).build());

    //prepare request
    final var request = UpdateItemRequest.builder()
      .tableName(subscriptionTableName)
      .key(itemKey)
      .attributeUpdates(itemValues)
      .build();

    //execute request
    final var future = db.updateItem(request);
    final var response = future.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
      System.out.println("Validated: " + subscriptionId + ", with ValicationCode: " + validationCode);
      return true;
    }
    else 
      return false;
  }

  private HashMap<String,AttributeValue> generateSubscriptionItemKey(String subscriptionId) {
    final var itemKey = new HashMap<String,AttributeValue>();
    itemKey.put("subscriptionId", AttributeValue.builder().s(subscriptionId).build());
    return itemKey;
  }

  @Override
  public List<Subscription> getAllSubscription() {

    //values to update in item
    final var itemValues = new HashMap<String, AttributeValueUpdate>();
    itemValues.put("isValidated", AttributeValueUpdate.builder().value(AttributeValue.builder().bool(true).build()).action(AttributeAction.PUT).build());

    //prepare request
    final var request = ScanRequest.builder()
      .tableName(subscriptionTableName)
      .build();

    //execute request
    final var future = db.scan(request);
    final var response = future.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK && response.hasItems()) {
      try {
        List<Subscription> result = response.items()
          .stream()
          .map(x -> new Subscription(x.get("subscriptionId").s(), x.get("feedUrl").s(), x.get("recipientEmail").s()))
          .toList();
        return result;
      } catch (Exception e) {
        System.out.println("something bad happened");
      }
    }
    return List.<Subscription>of();  }
}
