package com.rssmail.scheduler.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.rometools.rome.io.FeedException;
import com.rssmail.services.RssService.RssService;
import com.rssmail.utils.hashing.Node;

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
      final var feedItems = rssService.getFeed(feedUrl);

      //map feeditems to merkle trees for quick comparison
      final var feedItemHashes = feedItems.stream().map(i -> i.getHash()).toList();

      //loop through fake history, and remove from new feed, leaving behind only new items
      //var isUnchanged = fakeHistoryItemHashes.containsAll(feedItemHashes);

    } catch (IllegalArgumentException | FeedException | IOException e) {
      e.printStackTrace();
    }
  }
}

