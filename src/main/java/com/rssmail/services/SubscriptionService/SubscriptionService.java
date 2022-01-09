package com.rssmail.services.SubscriptionService;

public interface SubscriptionService {
  String createSubscription(String feedUrl, String recipientEmail);
  Boolean deleteSubscription(String subscriptionId, String recipientEmail);
  Boolean validateSubscription(String subscriptionid, String validationCode);
}
