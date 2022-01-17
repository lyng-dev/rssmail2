package com.rssmail.services.EmailService;

import org.springframework.http.HttpStatus;

import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

public class AwsSesEmailService implements EmailService {

  
  private SesAsyncClient client;
  private String senderEmail;
  private String recipientEmail;

  public AwsSesEmailService(SesAsyncClient sesAsyncClient, String senderEmail) {
    this.client = sesAsyncClient;
    this.senderEmail = senderEmail;
    this.recipientEmail = senderEmail;
  }

  @Override
  public void send(String message) {

    //prepare message
    Destination destination = Destination.builder()
      .toAddresses(recipientEmail)
      .build();

    Content content = Content.builder()
      .data(String.format("<html>%s</html>", message))
      .build();

    Content sub = Content.builder()
      .data("Testing ")
      .build();

    Body body = Body.builder()
      .html(content)
      .build();

    Message msg = Message.builder()
      .subject(sub)
      .body(body)
      .build();

    SendEmailRequest emailRequest = SendEmailRequest.builder()
      .destination(destination)
      .message(msg)
      .source(senderEmail)
      .build();

    //execute request
    // final var future = client.sendEmail(emailRequest);
    // final var response = future.join();

    // //if result is a valid
    // if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
      System.out.println("Sent email: " + message);
//      return subscriptionId;
    }
//    else 
//      return "";
  }
  
//}
