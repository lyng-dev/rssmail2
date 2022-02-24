package com.rssmail;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.scheduler.SubscriptionUpdateConsumer;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class App {

	private static Logger logger = LoggerFactory.getLogger(App.class);

  //TODO: Consider using Spring @Scheduled annotation instead of Quartz. All we 
  // need is simple timers. So basically Cron expressions, which is more or less
  // what Spring Scheduled does. Also more lightweight.
  public static void main(String[] args) {
		final ApplicationContext appContext = SpringApplication.run(App.class, args);
		try {
			logger.info(String.format("Using Spring Version %s:", SpringVersion.getVersion()));
			final var rssScheduler = (RssMailScheduler)appContext.getBean("rssMailScheduler");
			final var subscriptionUpdateConsumer = (SubscriptionUpdateConsumer)appContext.getBean("subscriptionUpdateConsumer");
			final var subscriptionService = (AwsSubscriptionService)appContext.getBean("awsSubscriptionService");
			final var filterMustBeValidated = true;

			//start scheduler for all validated subscriptions.
      final var allSubscriptions = subscriptionService.getAllSubscription(filterMustBeValidated);
      allSubscriptions.stream().forEach(subscription -> rssScheduler.start(subscription));

			//start consumer of subscriptionupdates
      subscriptionUpdateConsumer.start();

			//start the scheduler
			rssScheduler.startScheduler();

		} catch (Exception e) {
			logger.info("something failed during startup: " + e.getStackTrace());
			e.printStackTrace();
		}
    
	}

}