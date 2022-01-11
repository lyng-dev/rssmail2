package com.rssmail.scheduler;

import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ReadRssFeedJob;
import com.rssmail.services.FeedSubscriptionLastUpdatedContentStore.FeedSubscriptionLastUpdatedContentStore;
import com.rssmail.utils.hashing.HashTree;

import static org.quartz.JobBuilder.newJob;

import org.quartz.JobDataMap;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.Scheduler;

public class RssMailScheduler {

  private int counter = 0;

  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  private HashTree hashTree;

  private FeedSubscriptionLastUpdatedContentStore contentStore;

  public RssMailScheduler(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory, HashTree hashTree, FeedSubscriptionLastUpdatedContentStore contentStore) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    this.hashTree = hashTree;
    this.contentStore = contentStore;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }

  public void start(String feedUrl, String subscriptionId) throws SchedulerException {

    counter++; //update counter

    //incrementally name and group
    final var jobNumber = String.format("job%s", this.counter);
    final var triggerNumber = String.format("trigger%s", this.counter);
    final var groupName = "rssFeedReader";

    //map dynamic data
    final var jobDataMap = new JobDataMap();
    jobDataMap.put("feedUrl", feedUrl);
    jobDataMap.put("subscriptionId", subscriptionId);
    jobDataMap.put("hashTree", hashTree);
    jobDataMap.put("feedSubscriptionLastUpdatedContentStore", contentStore);

    //create job
    final var job = newJob(ReadRssFeedJob.class)
      .withIdentity(jobNumber, groupName)
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule().
      withIntervalInSeconds(5).
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