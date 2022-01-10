package com.rssmail.scheduler.jobs;

import com.rssmail.services.RssService.RssService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

@Component
public class ApplicationContextJobFactory implements JobFactory {

  private ApplicationContext appContext;

  public ApplicationContextJobFactory(ApplicationContext appContext) {
    this.appContext = appContext;
  }

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
    var jobDetail = bundle.getJobDetail();
    var job = (ReadRssFeedJob)appContext.getBean(jobDetail.getJobClass());
    return job;
  }
  
}
