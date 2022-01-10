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
      //load jobdata
      final var jobDataMap = context.getMergedJobDataMap();
      final var feedUrl = jobDataMap.get("feedUrl").toString();

      //extract feed
      System.out.println(String.format("\n------------------\n%s", rssService.read(feedUrl)));
    } catch (IllegalArgumentException | FeedException | IOException e) {
      e.printStackTrace();
    }
  }
}

