package com.rssmail.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Queue;

import com.rssmail.models.SubscriptionUpdate;
import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ConsumeSubscriptionUpdate;
import com.rssmail.services.EmailService.EmailService;
import com.rssmail.services.HandledSubscriptionFeedItemsContentStore.HandledSubscriptionFeedItemsContentStore;
import com.rssmail.services.SubscriptionService.SubscriptionService;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class SubscriptionUpdateConsumer {


  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  private int counter = 0;
  private HandledSubscriptionFeedItemsContentStore contentStore;
  private Queue<SubscriptionUpdate> subscriptionUpdatesQueue;
  private SubscriptionService subscriptionService;
  private EmailService emailService;


  public SubscriptionUpdateConsumer(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory, HandledSubscriptionFeedItemsContentStore contentStore, java.util.Queue<SubscriptionUpdate> queue, SubscriptionService subscriptionService, EmailService emailService) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    this.contentStore = contentStore;
    this.subscriptionUpdatesQueue = queue;
    this.subscriptionService = subscriptionService;
    this.emailService = emailService;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }


  public void start() throws SchedulerException {

    counter++; //update counter

    //incrementally name and group
    final var jobNumber = String.format("job%s", this.counter);
    final var triggerNumber = String.format("trigger%s", this.counter);
    final var groupName = "subscriptionUpdateConsumer";

    //map dynamic data
    final var jobDataMap = new JobDataMap();
    jobDataMap.put("feedSubscriptionLastUpdatedContentStore", contentStore);
    jobDataMap.put("subscriptionUpdatesQueue", subscriptionUpdatesQueue);
    jobDataMap.put("subscriptionService", subscriptionService);
    jobDataMap.put("emailService", emailService);

    //create job
    final var job = newJob(ConsumeSubscriptionUpdate.class)
      .withIdentity(jobNumber, groupName)
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule()
      .withIntervalInMilliseconds(100)
      .repeatForever();

    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity(triggerNumber, groupName)
      .withSchedule(schedule)
      .usingJobData(jobDataMap)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();  }
  
}
