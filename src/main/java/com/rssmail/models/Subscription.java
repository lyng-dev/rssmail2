package com.rssmail.models;

public class Subscription {

  private final String subscription;
  private final String feedUrl;
  private final String recipientEmail;

  public Subscription(String subscription, String feedUrl, String recipientEmail) {
    this.subscription = subscription;
    this.feedUrl = feedUrl;
    this.recipientEmail = recipientEmail;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public String getFeedUrl() {
    return feedUrl;
  }

  public String getSubscription() {
    return subscription;
  }
  
}
