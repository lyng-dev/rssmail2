package com.rssmail.services.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

public class AwsSesEmailService implements EmailService {

  private static Logger logger = LoggerFactory.getLogger(AwsSesEmailService.class);
  
  private SesAsyncClient client;
  private String senderEmail;
  private Boolean enabled;

  public AwsSesEmailService(Boolean enabled, SesAsyncClient sesAsyncClient, String senderEmail) {
    this.enabled = enabled;
    this.client = sesAsyncClient;
    this.senderEmail = senderEmail;
  }

  @Override
  public Boolean send(String recipientEmail, String title, String message) {

    //prepare message
    Destination destination = Destination.builder()
      .toAddresses(recipientEmail)
      .build();

    Content subject = Content.builder()
      .data(title)
      .build();

    Content content = Content.builder()
      .data(message)
      .build();

    Body body = Body.builder()
      .html(content)
      .build();

    Message msg = Message.builder()
      .subject(subject)
      .body(body)
      .build();

    SendEmailRequest emailRequest = SendEmailRequest.builder()
      .destination(destination)
      .message(msg)
      .source(senderEmail)
      .build();

    //execute request
    if (this.enabled) {
      final var future = client.sendEmail(emailRequest);
      final var response = future.join();
  
      //if result is a valid
      if (HttpStatus.valueOf(response.sdkHttpResponse().statusCode()) == HttpStatus.OK) {
        logger.info(String.format("Sent email: %s \n%s", recipientEmail, message));
        return true;
      }
      return false;
    } else {
      logger.info(String.format("Email disabled, would have sent email to: %s \n%s", recipientEmail, message));
      return true;
    }
  }
}
