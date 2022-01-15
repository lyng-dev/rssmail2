package com.rssmail.models;

public class SubscriptionUpdate {

  public FeedItem feedItem;
  public Subscription subscription;

  public SubscriptionUpdate(Subscription subscription, FeedItem feedItem) {
    this.subscription = subscription;
    this.feedItem = feedItem;
  }
  
}
