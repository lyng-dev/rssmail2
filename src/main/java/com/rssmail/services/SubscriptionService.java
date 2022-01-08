package com.rssmail.services;

public interface SubscriptionService {
  String createSubscription(String feedUrl, String recipientEmail);
  Boolean deleteSubscription(String subscriptionId, String recipientEmail);
  Boolean validateSubscription(String subscriptionid, String validationCode);
}
