package com.rssmail;

import com.rssmail.services.AwsSubscriptionService;
import com.rssmail.services.MyService;

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
		String[] beanNames = appContext.getBeanDefinitionNames();

		for (String beanName : beanNames) {
			System.out.println(beanName + " : " + appContext.getBean(beanName).getClass().toString());
		}

		var subscriptionService = (AwsSubscriptionService)appContext.getBean("awsSubscriptionService");
		subscriptionService.deleteSubscription("hello","goodbye");
	}
}

