package com.rssmail.services.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import com.rssmail.models.Subscription;

public interface SubscriptionService {
  public List<Subscription> getAllSubscription();
  public String createSubscription(String feedUrl, String recipientEmail);
  public Boolean deleteSubscription(String subscriptionId, String recipientEmail);
  public Boolean validateSubscription(String subscriptionid, String validationCode);
}
