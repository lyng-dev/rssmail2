package com.rssmail.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rssmail.TestAppConfig;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAppConfig.class)
public class AwsSubscriptionServiceTests {

 @Mock
 public DynamoDbAsyncClient dynamoDbAsyncClient;

 @Mock
 public EmailService emailService;

 @Mock RssMailScheduler rssMailScheduler;

 @Test
 public void canCallCreateSubscription() {

   //Arrange

   final String subscriptionTableName = "testing-subscriptions";
   final AwsSubscriptionService sut = new AwsSubscriptionService(dynamoDbAsyncClient, emailService, rssMailScheduler, subscriptionTableName);
   String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
   String recipient = "bob@example.org";

   //Act
   var result = sut.createSubscription(feedUrl, recipient);

   //Assert
   Assert.isTrue(result.length() > 0, "Expected a subscriptionId, but got an empty string.");
 }

//  @Test
//  public void GivenDeleteSubscriptionIsCalled_WhenValidInformationIsSupplied_ThenShouldDeleteSubscription() {
//    //Arrange
//    DeleteItemRequest mockDeleteItemRequest = mock(DeleteItemRequest.class);
//    CompletableFuture<DeleteItemResponse> mockDeleteItemResponseCompletableFuture = (CompletableFuture<DeleteItemResponse>)mock(CompletableFuture.class);
//    when(mockDeleteItemRequest.)
//    when(dynamoDbAsyncClient.deleteItem(deleteItemRequest)).then();
//    final AwsSubscriptionService sut = new AwsSubscriptionService(dynamoDbAsyncClient);
//    String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
//    String recipient = "bob@example.org";
//
//    //Act
//    boolean result = sut.deleteSubscription(feedUrl, recipient);
//
//    //Assert
//    Assert.isTrue(result, "Expected service call to return true");
//  }

}

