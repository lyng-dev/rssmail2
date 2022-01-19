package com.rssmail.services.SubscriptionService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rssmail.models.FeedItem;
import com.rssmail.models.Subscription;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.EmailService.EmailService;

import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Service
public class AwsSubscriptionService implements SubscriptionService {

  final private DynamoDbAsyncClient db;
  final private String subscriptionTableName;
  private EmailService emailService;
	private RssMailScheduler rssMailScheduler;

  public AwsSubscriptionService(DynamoDbAsyncClient dynamoDb, EmailService emailService, RssMailScheduler rssMailScheduler ,String subscriptionTableName) {
    this.db = dynamoDb;
    this.emailService = emailService;
		this.rssMailScheduler = rssMailScheduler;
    this.subscriptionTableName = subscriptionTableName;
  }

  //TODO: Should validate that the feed is returning good content.
  @Override
  public String createSubscription(@ModelAttribute("feedUrl") String feedUrl, 
                                   @ModelAttribute("recipientEmail") String recipientEmail) {
    //generate subscriptionId
    final var subscriptionId = UUID.randomUUID().toString();

    //generate default validationState
    final var defaultValidationState = false; //should be false, but for testing might be true.
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
    itemValues.put("handledFeedItems", AttributeValue.builder().s("[]").build());

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
      emailService.send(String.format("Your subscription for: %s has been created. Please validate your email by following this link: http://localhost:3000/validatesubscription?subscriptionId=%s&validationCode=%s", feedUrl, subscriptionId, validationCode));
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

    //get existing subscription
    final var existingSubscription = getSubscription(subscriptionId);

    //if already validated, succeed.
    if (existingSubscription.getIsValidated()) return true;
    
    //if validationCode is incorrect, the fail
    if (!existingSubscription.getValidationCode().equals(validationCode)) return false;

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
      try {
				rssMailScheduler.start(existingSubscription);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
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
  public Subscription getSubscription(String subscriptionId) {

    //object mapper
    final var objectMapper = new ObjectMapper();

    //item to retrieve
    final var itemKey = generateSubscriptionItemKey(subscriptionId);

    //prepare request
    final var request = GetItemRequest.builder()
      .tableName(subscriptionTableName)
      .key(itemKey)
      .build();

    //execute request
    final var future = db.getItem(request);
    final var response = future.join();

    //if result is a valid
    if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK && response.hasItem()) {
      try {
        final Map<String, AttributeValue> result = response.item();
        
          var subscription = new Subscription(
            result.get("subscriptionId").s(), 
            result.get("feedUrl").s(), 
            result.get("recipientEmail").s(),
            result.get("isValidated").bool(),
            result.get("validationCode").s(),
            (ArrayList<FeedItem>)objectMapper.readValue(result.get("handledFeedItems").s(), ArrayList.class)
          );

        System.out.print("Retrieved " + subscription.getHandledFeedItems().size() + " items from persistant storage");
        return subscription;
      } catch (Exception e) {
        System.out.println("something bad happened");
      }
    }

    //found no subscription
    return null;
  }

  @Override
  public List<Subscription> getAllSubscription(Boolean isValidated) {

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
        final List<Subscription> result = response.items()
          .stream()
          .takeWhile(x -> x.get("isValidated").bool() == isValidated) //check for validationState
          .map(x -> new Subscription(
            x.get("subscriptionId").s(), 
            x.get("feedUrl").s(), 
            x.get("recipientEmail").s(),
            x.get("isValidated").bool(),
            x.get("validationCode").s(),
            safeDeserializeFeedItems(x.get("handledFeedItems").s())))
          .toList();
        return result;
      } catch (Exception e) {
        System.out.println("something bad happened");
      }
    }
    return List.<Subscription>of();  }

  @Override
  public Boolean persistHandledFeedItems(String subscriptionId, ArrayList<FeedItem> feedItems) {
    
    //item to update
    final var itemKey = generateSubscriptionItemKey(subscriptionId);

    //serialize feedItems, or fail persist
    final var objectMapper = new ObjectMapper();
    String serializedFeedItems = "{}"; 
    var serializationFailed = true;
    try { 
      serializedFeedItems = objectMapper.writeValueAsString(feedItems);
      serializationFailed = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (serializationFailed) {
      System.out.println("Failed to serialize FeedItems. Operation halted. Nothing was persisted to storage.");
      return false;
    }

    //values to update in item
    final var itemValues = new HashMap<String, AttributeValueUpdate>();
    itemValues.put("handledFeedItems", AttributeValueUpdate.builder().value(AttributeValue.builder().s(serializedFeedItems).build()).action(AttributeAction.PUT).build());

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
      return true;
    }
    else 
      return false;
  }

  private ArrayList<FeedItem> safeDeserializeFeedItems(String feedItems) {
 
    //object mapper
    final var objectMapper = new ObjectMapper();

    //atttempt deserialization
    try {
        var result = (ArrayList<FeedItem>)objectMapper.readValue(feedItems, new TypeReference<ArrayList<FeedItem>>() {});
        return result;
    } catch (Exception e) {
      e.printStackTrace();
    }

    //failed 
    return new ArrayList<FeedItem>();
  }
}
