package com.rssmail;

import com.rssmail.scheduler.RssMailScheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		final ApplicationContext appContext = SpringApplication.run(App.class, args);

		try {
			var rssScheduler = (RssMailScheduler)appContext.getBean("rssMailScheduler");
			rssScheduler.start("https://aws.amazon.com/blogs/aws/feed/");
		} catch (Exception e) {
			System.out.println("something failed");
		}
	}
}