package com.rssmail.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import com.rssmail.TestAppConfig;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.utils.UUID;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAppConfig.class)
@ExtendWith(SpringExtension.class)
public class AwsSubscriptionServiceTests {

  @MockBean DynamoDbAsyncClient mockDb;

  @Test
  public void canCreateSubscriptionWithValidParameters() {

    //Arrange
    var mockEmailService = Mockito.mock(EmailService.class);
    var mockRssMailScheduler = Mockito.mock(RssMailScheduler.class);
    var mockUuid = Mockito.mock(UUID.class);
    String mockSubscriptionTableName = "testing-subscriptions";

    AwsSubscriptionService sut = new AwsSubscriptionService(
      mockDb, 
      mockEmailService, 
      mockRssMailScheduler, 
      mockUuid, 
      mockSubscriptionTableName);
    String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
    String recipient = "bob@example.org";
    String title = "RSSMAIL: Please validate your subscription";
    String expectedGuid = "7e5b24dc-b070-4fee-9a4e-0bfbcc1fe049";
    String messageForTheUser = "test message";
    var mockPutItemCompletableFuture = (CompletableFuture<PutItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockPutItemResponse = Mockito.mock(PutItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);

    when(mockUuid.random()).thenReturn(java.util.UUID.fromString(expectedGuid));
    when(mockEmailService.send(recipient, title, messageForTheUser)).thenReturn(true);
    when(mockDb.putItem(Mockito.any(PutItemRequest.class))).thenReturn(mockPutItemCompletableFuture);
    when(mockPutItemCompletableFuture.join()).thenReturn(mockPutItemResponse);
    when(mockPutItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);

    //Act
    var result = sut.createSubscription(feedUrl, recipient);

    //Assert
    verify(mockUuid, times(2)).random();
    verify(mockDb, times(1)).putItem(Mockito.any(PutItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockEmailService, times(1)).send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    Assert.isTrue(result.length() > 0, "Expected a subscriptionId, but got an empty string.");
    Assert.isTrue(result.equals(expectedGuid), String.format("Expected subscriptionId of '%s', but got '%s'", expectedGuid, result));
  }

  @Test
  public void createSubcriptionReturnsEmptyStringOnDbError() {

    //Arrange
    var mockEmailService = Mockito.mock(EmailService.class);
    var mockRssMailScheduler = Mockito.mock(RssMailScheduler.class);
    var mockUuid = Mockito.mock(UUID.class);
    String mockSubscriptionTableName = "testing-subscriptions";

    AwsSubscriptionService sut = new AwsSubscriptionService(
      mockDb, 
      mockEmailService, 
      mockRssMailScheduler, 
      mockUuid, 
      mockSubscriptionTableName);
    String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
    String recipient = "bob@example.org";
    String title = "RSSMAIL: Please validate your subscription";
    String expectedGuid = "7e5b24dc-b070-4fee-9a4e-0bfbcc1fe049";
    String messageForTheUser = "test message";
    var mockPutItemCompletableFuture = (CompletableFuture<PutItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockPutItemResponse = Mockito.mock(PutItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);

    when(mockUuid.random()).thenReturn(java.util.UUID.fromString(expectedGuid));
    when(mockEmailService.send(recipient, title, messageForTheUser)).thenReturn(true);
    when(mockDb.putItem(Mockito.any(PutItemRequest.class))).thenReturn(mockPutItemCompletableFuture);
    when(mockPutItemCompletableFuture.join()).thenReturn(mockPutItemResponse);
    when(mockPutItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

    //Act
    var result = sut.createSubscription(feedUrl, recipient);

    //Assert
    verify(mockUuid, times(2)).random();
    verify(mockDb, times(1)).putItem(Mockito.any(PutItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockEmailService, times(0)).send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    Assert.isTrue(result.length() == 0, String.format("Expected empty subscriptionId, but received '%s' instead", result));
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

