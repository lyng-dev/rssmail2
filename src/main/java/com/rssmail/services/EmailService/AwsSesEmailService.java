package com.rssmail.services.EmailService;

import software.amazon.awssdk.services.ses.SesAsyncClient;

public class AwsSesEmailService implements EmailService {

  
  private SesAsyncClient client;

  public AwsSesEmailService(SesAsyncClient sesAsyncClient) {
    this.client = sesAsyncClient;
  }

  @Override
  public void send(String message) {
    System.out.println(String.format("Would have sent message: %s", message));
  }
  
}
