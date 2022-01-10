package com.rssmail.scheduler;

import com.rssmail.scheduler.jobs.ApplicationContextJobFactory;
import com.rssmail.scheduler.jobs.ReadRssFeedJob;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.Scheduler;

public class RssMailScheduler {

  final private SchedulerFactory schedulerFactory;
  final private Scheduler scheduler;

  public RssMailScheduler(SchedulerFactory schedulerFactory, ApplicationContextJobFactory applicationContextJobFactory) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
    scheduler.setJobFactory(applicationContextJobFactory);
  }

  public void start() throws SchedulerException {
    final var job = newJob(ReadRssFeedJob.class)
      .withIdentity("job1", "group1")
      .build();  

    //prepare schedule
    final var schedule = simpleSchedule().
      withIntervalInSeconds(60).
      repeatForever();


    // Trigger the job to run on the next round minute
    final var trigger = newTrigger()
      .withIdentity("trigger1", "group1")
      .withSchedule(schedule)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
  }
}