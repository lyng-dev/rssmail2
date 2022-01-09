package com.rssmail.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // Say Hello to the World and display the date/time
    System.out.println("More cowbell..");
  }
}

