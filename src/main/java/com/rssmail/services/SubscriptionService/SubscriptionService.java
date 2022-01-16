package com.rssmail.services.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

import com.rssmail.models.FeedItem;
import com.rssmail.models.Subscription;

public interface SubscriptionService {
  public List<Subscription> getAllSubscription(Boolean isValidated);
  public String createSubscription(String feedUrl, String recipientEmail);
  public Boolean deleteSubscription(String subscriptionId, String recipientEmail);
  public Boolean validateSubscription(String subscriptionid, String validationCode);
  public Boolean persistHandledFeedItems(String subscriptionId, ArrayList<FeedItem> feedItems);
  public Subscription getSubscription(String subscriptionId);
}
