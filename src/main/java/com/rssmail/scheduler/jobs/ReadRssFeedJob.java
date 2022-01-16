package com.rssmail.scheduler.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import com.rometools.rome.io.FeedException;
import com.rssmail.models.FeedItem;
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
      System.out.print("1");
      final var feedUrl = jobDataMap.get("feedUrl").toString();
      System.out.print("2");
      final var subscription = (Subscription)jobDataMap.get("subscription");
      System.out.print("3");
      final var subscriptionUpdatesQueue = (Queue<SubscriptionUpdate>)jobDataMap.get("subscriptionUpdatesQueue");
      System.out.print("4");
      final var lastUpdatedContentStore = (HandledSubscriptionFeedItemsContentStore)jobDataMap.get("feedSubscriptionLastUpdatedContentStore");
      System.out.print("5>>>>");
      final var currentItems = lastUpdatedContentStore.get(subscription.getId());
      System.out.println(currentItems.size());
      currentItems.stream().forEach(x -> System.out.print(x));
      final var lastUpdatedItemHashes = lastUpdatedContentStore.get(subscription.getId())
                                                               .stream()
                                                               .map(x -> x.getHash())
                                                               .collect(Collectors.toList());

      System.out.print("6");
      //extract feed
      final var newFeedItems = rssService.getFeed(feedUrl);
      System.out.print("7");
      final var newFeedItemHashes = new ArrayList<String>(newFeedItems.stream()
                                                                      .map(i -> i.getHash())
                                                                      .toList());
      System.out.print("8");

      //loop through fake history, and remove from new feed, leaving behind only new items
      var isUnchanged = lastUpdatedItemHashes != null ? lastUpdatedItemHashes.containsAll(newFeedItemHashes) : true;
      System.out.print("9");

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

