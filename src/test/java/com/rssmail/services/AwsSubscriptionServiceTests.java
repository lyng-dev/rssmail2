package com.rssmail.services;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@SpringBootTest
public class AwsSubscriptionServiceTests {

  @Mock
  public DynamoDbAsyncClient dynamoDbAsyncClient;

  @Test
  public void canCallCreateSubscription() {
    //Arrange
    final AwsSubscriptionService sut = new AwsSubscriptionService(dynamoDbAsyncClient);
    String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
    String recipient = "bob@example.org";

    //Act
    boolean result = sut.createSubscription(feedUrl, recipient);

    //Assert
    Assert.isTrue(result, "Expected service call to return true");
  }

  @Test
  public void canCallDeleteSubscription() {
    //Arrange
    final AwsSubscriptionService sut = new AwsSubscriptionService(dynamoDbAsyncClient);
    String feedUrl = "https://aws.amazon.com/blogs/aws/feed/";
    String recipient = "bob@example.org";

    //Act
    boolean result = sut.deleteSubscription(feedUrl, recipient);

    //Assert
    Assert.isTrue(result, "Expected service call to return true");
  }

}
