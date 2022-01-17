package com.rssmail.models;

import java.util.ArrayList;

public class Subscription {

  private final String id;
  private final String feedUrl;
  private final String recipientEmail;
  private ArrayList<FeedItem> handledFeedItems;
  private Boolean isValidated;
  private String validationCode;

  public Subscription(String id, String feedUrl, String recipientEmail, Boolean isValidated, String validationCode) {
    this.id = id;
    this.feedUrl = feedUrl;
    this.recipientEmail = recipientEmail;
    this.isValidated = isValidated;
    this.validationCode = validationCode;
  }

  public String getValidationCode() {
    return validationCode;
  }

  public Boolean getIsValidated() {
    return isValidated;
  }

  public Subscription(String id, String feedUrl, String recipientEmail, Boolean isValidated, String validationCode, ArrayList<FeedItem> feedItems) {
    this(id, feedUrl, recipientEmail, isValidated, validationCode);
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
