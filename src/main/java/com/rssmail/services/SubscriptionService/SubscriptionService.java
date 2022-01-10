package com.rssmail.services.SubscriptionService;

public interface SubscriptionService {
  public String createSubscription(String feedUrl, String recipientEmail);
  public Boolean deleteSubscription(String subscriptionId, String recipientEmail);
  public Boolean validateSubscription(String subscriptionid, String validationCode);
}
