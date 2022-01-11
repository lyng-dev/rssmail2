package com.rssmail.scheduler.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.rometools.rome.io.FeedException;
import com.rssmail.services.FeedSubscriptionLastUpdatedContentStore.FeedSubscriptionLastUpdatedContentStore;
import com.rssmail.services.RssService.RssService;
import com.rssmail.utils.hashing.Node;

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
      final var subscriptionId = jobDataMap.get("subscriptionId").toString();
      final var feedSubscriptionLastUpdatedContentStore = (FeedSubscriptionLastUpdatedContentStore)jobDataMap.get("feedSubscriptionLastUpdatedContentStore");
      final var lastUpdatedFeedItemHashes = feedSubscriptionLastUpdatedContentStore.getFeedSubscription();
      final var currentJobSubscriptionFeedItemHashes = lastUpdatedFeedItemHashes.get(subscriptionId);
      //System.out.println(currentJobSubscriptionFeedItemHashes);

      //extract feed
      final var newFeedItems = rssService.getFeed(feedUrl);

      //map feeditems to hashTree for quick comparison
      final var newFeedItemHashes = new ArrayList<String>(newFeedItems.stream().map(i -> i.getHash()).toList());

      //loop through fake history, and remove from new feed, leaving behind only new items
      var isUnchanged = currentJobSubscriptionFeedItemHashes != null ? currentJobSubscriptionFeedItemHashes.containsAll(newFeedItemHashes) : true;

      if (!isUnchanged) {
        var updatedFeedItemHashes = new ArrayList<String>(newFeedItemHashes);
        updatedFeedItemHashes.removeAll(currentJobSubscriptionFeedItemHashes);
        System.out.print("FEED HAS CHANGED. Item hash added is: ");
        System.out.println(updatedFeedItemHashes);
        System.out.println("Which is: ");
        newFeedItems.stream().filter(i -> updatedFeedItemHashes.contains(i.getHash())).forEach(i -> System.out.println(i.getTitle()));
      }

      FeedSubscriptionLastUpdatedContentStore.put(subscriptionId, newFeedItemHashes);

    } catch (IllegalArgumentException | FeedException | IOException e) {
      e.printStackTrace();
    }
  }
}

