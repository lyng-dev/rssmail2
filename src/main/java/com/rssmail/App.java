package com.rssmail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.scheduler.SubscriptionUpdateConsumer;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.utils.hashing.HashTree;
import com.rssmail.utils.hashing.Node;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

  //TODO: Consider using Spring @Scheduled annotation instead of Quartz. All we 
  // need is simple timers. So basically Cron expressions, which is more or less
  // what Spring Scheduled does. Also more lightweight.
  public static void main(String[] args) {
		final ApplicationContext appContext = SpringApplication.run(App.class, args);
		try {
			final var rssScheduler = (RssMailScheduler)appContext.getBean("rssMailScheduler");
			final var subscriptionUpdateConsumer = (SubscriptionUpdateConsumer)appContext.getBean("subscriptionUpdateConsumer");
			final var subscriptionService = (AwsSubscriptionService)appContext.getBean("awsSubscriptionService");
			final var filterMustBeValidated = true;

      final var allSubscriptions = subscriptionService.getAllSubscription(filterMustBeValidated);
      allSubscriptions.stream().forEach(subscription -> {
				try {
					rssScheduler.start(subscription);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});

      subscriptionUpdateConsumer.start();
		} catch (Exception e) {
			System.out.println("something failed");
		}
    
	}

}