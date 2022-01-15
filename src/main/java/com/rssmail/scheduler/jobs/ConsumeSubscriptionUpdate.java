package com.rssmail.scheduler.jobs;

import java.util.Queue;

import com.rssmail.services.SubscriptionService.SubscriptionService;
import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class ConsumeSubscriptionUpdate implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    System.out.print("x");

      //load jobdata
      final var jobDataMap = context.getMergedJobDataMap();
      final var lastUpdatedContentStore = (HandledSubscriptionFeedItemsContentStore)jobDataMap.get("feedSubscriptionLastUpdatedContentStore");
      final var subscriptionService = (SubscriptionService)jobDataMap.get("subscriptionService");


      //read data
      final var subscriptionUpdatesQueue = (Queue<SubscriptionUpdate>)jobDataMap.get("subscriptionUpdatesQueue");
      var update = subscriptionUpdatesQueue.poll();
      if (update != null) {
        var feedItem = update.feedItem;
        var subscription = update.subscription;
        System.out.println("Popped item off the queue: " + feedItem.getTitle());
        System.out.println(String.format("Handled SubscriptionId: %s, RecipientEmail: %s, Title: %s", subscription.getId(), subscription.getRecipientEmail(), feedItem.getTitle()));
        lastUpdatedContentStore.get(subscription.getId()).add(feedItem);
        System.out.println("Added item to ContentStore");
        subscriptionService.persistHandledFeedItems(subscription.getId(), lastUpdatedContentStore.get(subscription.getId()));
        System.out.println("Persisted handled items.");
      }


  }
  
}
