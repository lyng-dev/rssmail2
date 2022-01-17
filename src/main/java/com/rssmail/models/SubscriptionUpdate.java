package com.rssmail.models;

public class SubscriptionUpdate {

  public FeedItem feedItem;
  public Subscription subscription;
  private Boolean isBootStrapping;

  public SubscriptionUpdate(Subscription subscription, FeedItem feedItem, Boolean isBootStrapping) {
    this.subscription = subscription;
    this.feedItem = feedItem;
    this.isBootStrapping = isBootStrapping;
  }

  public Boolean getIsBootStrapping() {
    return isBootStrapping;
  }
  
}
