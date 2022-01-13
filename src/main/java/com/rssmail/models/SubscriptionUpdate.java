package com.rssmail.models;

public class SubscriptionUpdate {

  public FeedItem feedItem;
  public String subscriptionId;

  public SubscriptionUpdate(String subscriptionId, FeedItem feedItem) {
    this.subscriptionId = subscriptionId;
    this.feedItem = feedItem;
  }
  
}
