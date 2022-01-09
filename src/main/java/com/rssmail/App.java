package com.rssmail;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.AwsSubscriptionService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		ApplicationContext appContext = SpringApplication.run(App.class, args);
		// String[] beanNames = appContext.getBeanDefinitionNames();

		// for (String beanName : beanNames) {
		// 	System.out.println(beanName + " : " + appContext.getBean(beanName).getClass().toString());
		// }

		try {
			var service = (RssMailScheduler)appContext.getBean("rssMailScheduler");
			service.start();
		} catch (Exception e) {
			System.out.println("something failed");
		}

		// var subscriptionService = (AwsSubscriptionService)appContext.getBean("awsSubscriptionService");
		// var subscriptionId = subscriptionService.createSubscription("http://google.com", "s@sunlyng.dk");
		// subscriptionService.validateSubscription(subscriptionId,"abc");
		// subscriptionService.deleteSubscription(subscriptionId,"s@sunlyng");
	}
}