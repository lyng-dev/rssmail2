package com.rssmail.scheduler;

import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.DateBuilder;
import org.quartz.Scheduler;

public class RssMailScheduler {

  SchedulerFactory schedulerFactory;
  Scheduler scheduler;

  public RssMailScheduler(SchedulerFactory schedulerFactory) throws SchedulerException {
    this.schedulerFactory = schedulerFactory;
    scheduler = (Scheduler) this.schedulerFactory.getScheduler();
  }

  public void start() throws SchedulerException {
    JobDetail job = JobBuilder
      .newJob(HelloJob.class)
      .withIdentity("job1", "group1")
      .build();  

    //prepare schedule
    var schedule = simpleSchedule().
      withIntervalInSeconds(5).
      repeatForever();


    // Trigger the job to run on the next round minute
    Trigger trigger = newTrigger()
      .withIdentity("trigger1", "group1")
      .withSchedule(schedule)
      .build();
      
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
  }
}