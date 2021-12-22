package com.rssmail.services;

public interface SubscriptionService {
  boolean createSubscription(String feedUrl, String recipient);
  boolean deleteSubscription(String tokenId, String recipient);
}
