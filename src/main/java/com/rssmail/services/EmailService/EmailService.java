package com.rssmail.services.EmailService;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {

  public Boolean send(String recipientEmail, String title, String message);
  
}
  