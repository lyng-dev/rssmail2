package com.rssmail.services;

public interface SubscriptionService {
  void createSubscription(String feedUrl, String recipient);
  void deleteSubscription(String tokenId, String recipient);
}
