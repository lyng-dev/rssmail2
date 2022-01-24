package com.rssmail.scheduler.jobs;

import java.util.Queue;

import com.rssmail.services.SubscriptionService.SubscriptionService;
import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class ConsumeSubscriptionUpdate implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

      //load jobdata
      final var jobDataMap = context.getMergedJobDataMap();
      final var lastUpdatedContentStore = (HandledSubscriptionFeedItemsContentStore)jobDataMap.get("feedSubscriptionLastUpdatedContentStore");
      final var subscriptionService = (SubscriptionService)jobDataMap.get("subscriptionService");
      final var emailService = (EmailService)jobDataMap.get("emailService");


      //read data
      final var subscriptionUpdatesQueue = (Queue<SubscriptionUpdate>)jobDataMap.get("subscriptionUpdatesQueue");
      var update = subscriptionUpdatesQueue.poll();
      if (update != null) {
        var feedItem = update.feedItem;
        var subscription = update.subscription;
        //always assume that we are successful in sending the update
        lastUpdatedContentStore.get(subscription.getId()).add(feedItem);
        //then persist state
        subscriptionService.persistHandledFeedItems(subscription.getId(), lastUpdatedContentStore.get(subscription.getId()));
        //then send the actual update. 
        
        if (!update.getIsBootStrapping())
          emailService.send(
            subscription.getRecipientEmail(), 
            feedItem.getTitle(), 
            String.format(
"""
<html>
<body>
<div>Hi! - A feed you are subscribing to has updated.</div>
<br/>
<div><a href=\"%s\">%s</a></div>
<br/>
<div>%s</div>
<br/>
<div>To stop your subscription: http://localhost:3000/deletesubscription?subscriptionId=%s&recipientEmail=%s</div>
<br/>
<div>//RSSMAIL</div></html>
""", feedItem.getUri(), feedItem.getTitle(), feedItem.getDescription(), subscription.getId(), subscription.getRecipientEmail()));
        else
          System.out.println("Skipped sending, because we are bootstrapping.");
      }


  }
  
}
