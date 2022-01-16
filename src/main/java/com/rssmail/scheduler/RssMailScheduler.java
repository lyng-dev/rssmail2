package com.rssmail.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Queue;

import com.rssmail.models.FeedItem;
import com.rssmail.models.Subscription;
import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ReadRssFeedJob;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.utils.hashing.HashTree;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class RssMailScheduler {

  private int counter = 0;

  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  private HashTree hashTree;

  private HandledSubscriptionFeedItemsContentStore contentStore;

  private Queue<SubscriptionUpdate> subscriptionUpdatesQueue;

  public RssMailScheduler(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory, HashTree hashTree, HandledSubscriptionFeedItemsContentStore contentStore, Queue<SubscriptionUpdate> queue) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    this.hashTree = hashTree;
    this.contentStore = contentStore;
    this.subscriptionUpdatesQueue = queue;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }

  public void start(Subscription subscription) throws SchedulerException {
    counter++; //update counter

    //incrementally name and group
    final var jobNumber = String.format("job%s", this.counter);
    final var triggerNumber = String.format("trigger%s", this.counter);
    final var groupName = "rssFeedReader";

    //populate the contentstore
    var handledFeedItems = subscription.getHandledFeedItems();
    contentStore.put(subscription.getId(), handledFeedItems);

    //map dynamic data
    final var jobDataMap = new JobDataMap();
    jobDataMap.put("feedUrl", subscription.getFeedUrl());
    jobDataMap.put("subscription", subscription);
    jobDataMap.put("hashTree", hashTree);
    jobDataMap.put("feedSubscriptionLastUpdatedContentStore", contentStore);
    jobDataMap.put("subscriptionUpdatesQueue", subscriptionUpdatesQueue);

    //create job
    final var job = newJob(ReadRssFeedJob.class)
      .withIdentity(jobNumber, groupName)
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule().
      withIntervalInSeconds(30).
      repeatForever();

    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity(triggerNumber, groupName)
      .withSchedule(schedule)
      .usingJobData(jobDataMap)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
  }
}