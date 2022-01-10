package com.rssmail.scheduler;

import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ReadRssFeedJob;
import static org.quartz.JobBuilder.newJob;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.Scheduler;

public class RssMailScheduler {

  private int counter = 0;

  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  public RssMailScheduler(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }

  public void start(String feedUrl) throws SchedulerException {

    counter++;

    //assign jobnumber
    final var jobNumber = String.format("job%s", this.counter);
    final var triggerNumber = String.format("trigger%s", this.counter);

    //map dynamic data
    final var jobDataMap = new JobDataMap();
    jobDataMap.put("feedUrl", feedUrl);

    //create job
    final var job = newJob(ReadRssFeedJob.class)
      .withIdentity(jobNumber, "group1")
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule().
      withIntervalInSeconds(5).
      repeatForever();

    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity(triggerNumber, "group1")
      .withSchedule(schedule)
      .usingJobData(jobDataMap)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
  }
}