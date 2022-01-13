package com.rssmail.services.EmailService;

import org.springframework.stereotype.Component;

@Component
public class EmailService {

  public EmailService() {
  }

  public void send(String message) {
    System.out.println("Would have sent: " + message);
  }
  
}
  