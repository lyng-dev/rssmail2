package com.rssmail.scheduler.jobs;

import java.io.IOException;
import com.rometools.rome.io.FeedException;
import com.rssmail.services.RssService.RssService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class ReadRssFeedJob implements Job {

  final private RssService rssService;

  public ReadRssFeedJob(RssService rssService) {
    this.rssService = rssService;
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      System.out.println(String.format("\n------------------\n%s", rssService.read()));
    } catch (IllegalArgumentException | FeedException | IOException e) {
      e.printStackTrace();
    }
  }
}

