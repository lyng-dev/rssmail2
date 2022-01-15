package com.rssmail.scheduler.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import com.rometools.rome.io.FeedException;
import com.rssmail.models.Subscription;
import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.services.RssService.RssService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class ReadRssFeedJob implements Job {

  final private RssService rssService;

  public ReadRssFeedJob(RssService rssService) {
    this.rssService = rssService;
  } 

  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      System.out.print(".");

      //load jobdata
      final var jobDataMap = context.getMergedJobDataMap();
      final var feedUrl = jobDataMap.get("feedUrl").toString();
      final var subscription = (Subscription)jobDataMap.get("subscription");
      final var subscriptionUpdatesQueue = (Queue<SubscriptionUpdate>)jobDataMap.get("subscriptionUpdatesQueue");
      final var lastUpdatedContentStore = (HandledSubscriptionFeedItemsContentStore)jobDataMap.get("feedSubscriptionLastUpdatedContentStore");
      final var lastUpdatedItemHashes = lastUpdatedContentStore.get(subscription.getId())
                                                               .stream()
                                                               .map(x -> x.getHash())
                                                               .collect(Collectors.toList());

      //extract feed
      final var newFeedItems = rssService.getFeed(feedUrl);
      final var newFeedItemHashes = new ArrayList<String>(newFeedItems.stream()
                                                                      .map(i -> i.getHash())
                                                                      .toList());

      //loop through fake history, and remove from new feed, leaving behind only new items
      var isUnchanged = lastUpdatedItemHashes != null ? lastUpdatedItemHashes.containsAll(newFeedItemHashes) : true;

      if (!isUnchanged) {
        var updatedFeedItemHashes = new ArrayList<String>(newFeedItemHashes);
        updatedFeedItemHashes.removeAll(lastUpdatedItemHashes);
        System.out.print("FEED HAS CHANGED. Item hash added is: ");
        System.out.println(updatedFeedItemHashes);
        System.out.println("Which is: ");
        newFeedItems.stream().filter(i -> updatedFeedItemHashes.contains(i.getHash())).forEach(i -> {
          System.out.println(i.getTitle());
          var subscriptionUpdate = new SubscriptionUpdate(subscription, i);
          subscriptionUpdatesQueue.offer(subscriptionUpdate);
        });
        System.out.print("Queue updated, now contains: ");
        System.out.println(String.format("%s items", subscriptionUpdatesQueue.size()));
      }

    } catch (IllegalArgumentException | FeedException | IOException e) {
      e.printStackTrace();
    }
  }
}

