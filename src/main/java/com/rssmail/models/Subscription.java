package com.rssmail.models;

import java.util.ArrayList;

public class Subscription {

  private final String id;
  private final String feedUrl;
  private final String recipientEmail;
  private ArrayList<FeedItem> handledFeedItems;

  public Subscription(String id, String feedUrl, String recipientEmail) {
    this.id = id;
    this.feedUrl = feedUrl;
    this.recipientEmail = recipientEmail;
  }

  public Subscription(String id, String feedUrl, String recipientEmail, ArrayList<FeedItem> feedItems) {
    this(id, feedUrl, recipientEmail);
    this.handledFeedItems = feedItems;
  }

  public String getId() {
    return id;
  }

  public ArrayList<FeedItem> getHandledFeedItems() {
    return handledFeedItems;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public String getFeedUrl() {
    return feedUrl;
  }
  
}
