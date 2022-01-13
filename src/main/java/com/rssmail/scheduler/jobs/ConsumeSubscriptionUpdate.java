package com.rssmail.scheduler.jobs;

import java.util.Queue;

import com.rssmail.models.SubscriptionUpdate;

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

      //read data
      final var subscriptionUpdatesQueue = (Queue<SubscriptionUpdate>)jobDataMap.get("subscriptionUpdatesQueue");
      var possibleUpdate = subscriptionUpdatesQueue.poll();
      if (possibleUpdate != null) {
        System.out.println("Popped item off the queue: " + possibleUpdate.feedItem.getTitle());
      }

  }
  
}
