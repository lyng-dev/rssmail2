package com.rssmail;

import com.rssmail.services.AwsSubscriptionServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

	@Autowired
	private AwsSubscriptionServiceImpl service;

	public static void main(String[] args) {
		ApplicationContext appContext =	SpringApplication.run(App.class, args);

		verifyConnection();

		// for (String s : appContext.getBeanDefinitionNames()) {
		// 		System.out.println(s);
		// }
	}

	private static void verifyConnection() {
		System.out.println("Verifying connection... OK");
	}

}
