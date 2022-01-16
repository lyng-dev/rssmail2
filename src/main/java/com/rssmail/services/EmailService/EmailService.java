package com.rssmail.services.EmailService;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {

  public void send(String message);
  
}
  