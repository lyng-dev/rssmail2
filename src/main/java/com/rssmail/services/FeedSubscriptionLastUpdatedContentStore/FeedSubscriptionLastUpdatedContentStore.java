package com.rssmail.services.FeedSubscriptionLastUpdatedContentStore;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class FeedSubscriptionLastUpdatedContentStore {

  private static HashMap<String, ArrayList<String>> feedSubscriptions = new HashMap<>();

  public FeedSubscriptionLastUpdatedContentStore() {
  }

  public static void put(String subscriptionId, ArrayList<String> feedSubscriptionLastUpdatedContent) {
    feedSubscriptions.put(subscriptionId, feedSubscriptionLastUpdatedContent);
  }

  public static HashMap<String, ArrayList<String>> getFeedSubscription() {
    return feedSubscriptions;
  }

}
