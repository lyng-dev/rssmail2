package com.rssmail.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import com.rssmail.TestAppConfig;
import com.rssmail.models.Subscription;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.utils.UUID;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAppConfig.class)
@ExtendWith(SpringExtension.class)
public class AwsSubscriptionServiceTests {
  
  private EmailService mockEmailService;
  private RssMailScheduler mockRssMailScheduler;
  private UUID mockUuid;
  private String mockSubscriptionTableName;
  private AwsSubscriptionService sut;
  
  @MockBean DynamoDbAsyncClient mockDb;

  @BeforeEach
  private void beforeEach() {
    mockEmailService = Mockito.mock(EmailService.class);
    mockRssMailScheduler = Mockito.mock(RssMailScheduler.class);
    mockUuid = Mockito.mock(UUID.class);
    mockSubscriptionTableName = "testing-subscriptions";
    sut = new AwsSubscriptionService(
      mockDb, 
      mockEmailService, 
      mockRssMailScheduler, 
      mockUuid, 
      mockSubscriptionTableName);
  }

  @Test
  public void canValidateSubscriptionWithValidParamets() {

    //Arrange
    var sutSpy = Mockito.spy(sut);
  
    String subscriptionId = "testId";
    String validationCode = "validationCode";
    var fakeSubscription = new Subscription(subscriptionId, "feedUrl", "bob@example.org", false, "validationCode");
    var mockUpdateItemCompletableFuture = (CompletableFuture<UpdateItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockUpdateItemResponse = Mockito.mock(UpdateItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);
    
    when(mockDb.updateItem(Mockito.any(UpdateItemRequest.class))).thenReturn(mockUpdateItemCompletableFuture);
    when(mockUpdateItemCompletableFuture.join()).thenReturn(mockUpdateItemResponse);
    Mockito.doReturn(fakeSubscription).when(sutSpy).getSubscription(subscriptionId);
    when(mockUpdateItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
    
    //Act
    var result = sutSpy.validateSubscription(subscriptionId, validationCode);
    
    //Assert
    verify(sutSpy, times(1)).getSubscription(anyString());
    verify(mockDb, times(1)).updateItem(Mockito.any(UpdateItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockUpdateItemCompletableFuture, times(1)).join();
    Assert.isTrue(result, String.format("Expected a 'true' result, but got '%s'", result.toString()));
  }

  @Test
  public void validateSubscriptionReturnsFalseOnDbError() {

    //Arrange
    var sutSpy = Mockito.spy(sut);
  
    String subscriptionId = "testId";
    String validationCode = "validationCode";
    var fakeSubscription = new Subscription(subscriptionId, "feedUrl", "bob@example.org", false, "validationCode");
    var mockUpdateItemCompletableFuture = (CompletableFuture<UpdateItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockUpdateItemResponse = Mockito.mock(UpdateItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);
    
    when(mockDb.updateItem(Mockito.any(UpdateItemRequest.class))).thenReturn(mockUpdateItemCompletableFuture);
    when(mockUpdateItemCompletableFuture.join()).thenReturn(mockUpdateItemResponse);
    Mockito.doReturn(fakeSubscription).when(sutSpy).getSubscription(subscriptionId);
    when(mockUpdateItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
    
    //Act
    var result = sutSpy.validateSubscription(subscriptionId, validationCode);
    
    //Assert
    verify(sutSpy, times(1)).getSubscription(anyString());
    verify(mockDb, times(1)).updateItem(Mockito.any(UpdateItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockUpdateItemCompletableFuture, times(1)).join();
    Assert.isTrue(!result, String.format("Expected a 'false' result, but got '%s'", result.toString()));
  }

  @Test
  public void canDeleteSubscriptionWithValidParamets() {

    //Arrange
    String recipient = "bob@example.org";
    String subscriptionId = "testId";
    var mockDeleteItemCompletableFuture = (CompletableFuture<DeleteItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockDeleteItemResponse = Mockito.mock(DeleteItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);

    when(mockDb.deleteItem(Mockito.any(DeleteItemRequest.class))).thenReturn(mockDeleteItemCompletableFuture);
    when(mockDeleteItemCompletableFuture.join()).thenReturn(mockDeleteItemResponse);
    when(mockDeleteItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);

    //Act
    var result = sut.deleteSubscription(subscriptionId, recipient);

    //Assert
    verify(mockDb, times(1)).deleteItem(Mockito.any(DeleteItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockDeleteItemCompletableFuture, times(1)).join();
    Assert.isTrue(result, String.format("Expected a 'true' result, but got '%s'", result.toString()));
  }

  @Test
  public void deleteSubscriptionReturnsEmptyStringOnDbError() {

    //Arrange
    AwsSubscriptionService sut = new AwsSubscriptionService(
      mockDb, 
      mockEmailService, 
      mockRssMailScheduler, 
      mockUuid, 
      mockSubscriptionTableName);

    String recipient = "bob@example.org";
    String subscriptionId = "testId";
    var mockDeleteItemCompletableFuture = (CompletableFuture<DeleteItemResponse>)Mockito.mock(CompletableFuture.class);
    var mockDeleteItemResponse = Mockito.mock(DeleteItemResponse.class);
    var mockSdkHttpResponse = Mockito.mock(SdkHttpResponse.class);

    when(mockDb.deleteItem(Mockito.any(DeleteItemRequest.class))).thenReturn(mockDeleteItemCompletableFuture);
    when(mockDeleteItemCompletableFuture.join()).thenReturn(mockDeleteItemResponse);
    when(mockDeleteItemResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockSdkHttpResponse.statusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

    //Act
    var result = sut.deleteSubscription(subscriptionId, recipient);

    //Assert
    verify(mockDb, times(1)).deleteItem(Mockito.any(DeleteItemRequest.class));
    verify(mockSdkHttpResponse, times(1)).statusCode();
    verify(mockDeleteItemCompletableFuture, times(1)).join();
    Assert.isTrue(!result, String.format("Expected a 'false' result, but got '%s'", result.toString()));
  }

  @Test
  public void canCreateSubscriptionWithValidParameters() {

    //Arrange
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
    verify(mockPutItemCompletableFuture, times(1)).join();
    Assert.isTrue(result.length() > 0, "Expected a subscriptionId, but got an empty string.");
    Assert.isTrue(result.equals(expectedGuid), String.format("Expected subscriptionId of '%s', but got '%s'", expectedGuid, result));
  }

  @Test
  public void createSubcriptionReturnsEmptyStringOnDbError() {

    //Arrange
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
    verify(mockPutItemCompletableFuture, times(1)).join();
    Assert.isTrue(result.length() == 0, String.format("Expected empty subscriptionId, but received '%s' instead", result));
  }


}

