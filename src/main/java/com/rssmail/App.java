package com.rssmail;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		final ApplicationContext appContext = SpringApplication.run(App.class, args);
		try {
			final var rssScheduler = (RssMailScheduler)appContext.getBean("rssMailScheduler");
			final var subscriptionService = (AwsSubscriptionService)appContext.getBean("awsSubscriptionService");
			final var filterMustBeValidated = true;
			subscriptionService.getAllSubscription(filterMustBeValidated).stream().forEach(x -> {
				try {
					rssScheduler.start(x.getFeedUrl());
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			System.out.println("something failed");
		}
	}
}