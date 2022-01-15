package com.rssmail.services.HandledSubscriptionFeedItemsContentStore;

import java.util.ArrayList;
import java.util.HashMap;

import com.rssmail.models.FeedItem;

import org.springframework.stereotype.Component;

@Component
public class HandledSubscriptionFeedItemsContentStore {

  private HashMap<String, ArrayList<FeedItem>> feedSubscriptions = new HashMap<>();

  public HandledSubscriptionFeedItemsContentStore() {
  }

  public void put(String subscriptionId, ArrayList<FeedItem> feedItems) {
    feedSubscriptions.put(subscriptionId, feedItems);
  }

  public ArrayList<FeedItem> get(String subscriptionId) {
    var feedItems = feedSubscriptions.get(subscriptionId);
    return feedItems != null ? feedItems : new ArrayList<FeedItem>();
  }

}
