package com.rssmail;

import com.rssmail.services.AwsSubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import software.amazon.awssdk.regions.Region;

@SpringBootApplication
public class App {

	@Autowired
	public AwsSubscriptionService awsSubscriptionService;

	public static void main(String[] args) {
		ApplicationContext appContext = SpringApplication.run(App.class, args);
	}

}
