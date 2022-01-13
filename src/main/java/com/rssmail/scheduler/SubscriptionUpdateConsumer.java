package com.rssmail.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Queue;

import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ConsumeSubscriptionUpdate;
import com.rssmail.services.FeedSubscriptionLastUpdatedContentStore.FeedSubscriptionLastUpdatedContentStore;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class SubscriptionUpdateConsumer {


  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  private int counter = 0;
  private FeedSubscriptionLastUpdatedContentStore contentStore;
  private Queue<SubscriptionUpdate> subscriptionUpdatesQueue;


  public SubscriptionUpdateConsumer(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory, FeedSubscriptionLastUpdatedContentStore contentStore, java.util.Queue<SubscriptionUpdate> queue) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    this.contentStore = contentStore;
    this.subscriptionUpdatesQueue = queue;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }


  public void start() throws SchedulerException {

    System.out.println("Starting updates consumer..");

    counter++; //update counter

    //incrementally name and group
    final var jobNumber = String.format("job%s", this.counter);
    final var triggerNumber = String.format("trigger%s", this.counter);
    final var groupName = "subscriptionUpdateConsumer";

    //map dynamic data
    final var jobDataMap = new JobDataMap();
    jobDataMap.put("feedSubscriptionLastUpdatedContentStore", contentStore);
    jobDataMap.put("subscriptionUpdatesQueue", subscriptionUpdatesQueue);
    //create job
    final var job = newJob(ConsumeSubscriptionUpdate.class)
      .withIdentity(jobNumber, groupName)
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule().
      withIntervalInSeconds(17).
      repeatForever();

    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity(triggerNumber, groupName)
      .withSchedule(schedule)
      .usingJobData(jobDataMap)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();  }
  
}
