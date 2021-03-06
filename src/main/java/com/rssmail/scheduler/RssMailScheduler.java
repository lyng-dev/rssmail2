package com.rssmail.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Queue;

import com.rssmail.models.Subscription;
import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.jobs.ReadRssFeedJob;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.utils.hashing.HashTree;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RssMailScheduler {
	private static Logger logger = LoggerFactory.getLogger(RssMailScheduler.class);

  final private String groupName = "rssFeedReader";

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

  public Boolean stop(String subscriptionId) {
    try {
      var jobKey = new JobKey(subscriptionId, this.groupName);
      logger.info("Stopping Job: " + jobKey.toString());
      return scheduler.deleteJob(jobKey);
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
    }
    return false;
  }

  public String start(Subscription subscription) {
    //incrementally name and group
    final var jobId = subscription.getId();
    final var triggerId = subscription.getId();

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
      .withIdentity(jobId, groupName)
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule()
      .withIntervalInSeconds(30)
      .repeatForever();

    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity(triggerId, groupName)
      .withSchedule(schedule)
      .usingJobData(jobDataMap)
      .build();
      

    try {
      var jobKey = job.getKey().toString();
      logger.info(String.format("Scheduling job %s", jobKey));
      scheduler.scheduleJob(job, trigger);
      logger.info("Starting Job: " + jobKey);
        return jobKey;
      } catch (SchedulerException e) {
        logger.error(e.getMessage(), e);
      }
    return "";
  }

  public Boolean startScheduler() {
    try {
      scheduler.start();
      return true;
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
    }
    return false;
  }
}