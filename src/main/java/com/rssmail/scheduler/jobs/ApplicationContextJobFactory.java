package com.rssmail.scheduler.jobs;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

@Component
public class ApplicationContextJobFactory implements JobFactory {

  final private ApplicationContext appContext;

  public ApplicationContextJobFactory(ApplicationContext appContext) {
    this.appContext = appContext;
  }

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
    final var jobDetail = bundle.getJobDetail();
    final var job = (ReadRssFeedJob)appContext.getBean(jobDetail.getJobClass());
    return job;
  }

  public Job newJobWithParameters(TriggerFiredBundle bundle, Scheduler scheduler, String feedUrl) throws SchedulerException {
    final var jobDetail = bundle.getJobDetail();
    final var job = (ReadRssFeedJob)appContext.getBean(jobDetail.getJobClass(), feedUrl);
    return job;
  }
  
}
