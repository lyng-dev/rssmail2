package com.rssmail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.SubscriptionService.AwsSubscriptionService;
import com.rssmail.utils.hashing.MerkleTree;
import com.rssmail.utils.hashing.Node;

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
		// ArrayList<String> dataBlocks = new ArrayList<>();
		// dataBlocks.add("Captain America");
		// dataBlocks.add("Iron Man");
		// dataBlocks.add("God of thunder");
		// dataBlocks.add("Doctor strange");
		// Node root = MerkleTree.generateTree(dataBlocks);
		// printLevelOrderTraversal(root);
	}

  // private static void printLevelOrderTraversal(Node root) {
  //     if (root == null) {
  //         return;
  //     }

  //     if ((root.getLeft() == null && root.getRight() == null)) {
  //         System.out.println(root.getHash());
  //     }
  //     Queue<Node> queue = new LinkedList<>();
  //     queue.add(root);
  //     queue.add(null);

  //     while (!queue.isEmpty()) {
  //         Node node = queue.poll();
  //         if (node != null) {
  //             System.out.println(node.getHash());
  //         } else {
  //             System.out.println();
  //             if (!queue.isEmpty()) {
  //                 queue.add(null);
  //             }
  //         }

  //         if (node != null && node.getLeft() != null) {
  //             queue.add(node.getLeft());
  //         }

  //         if (node != null && node.getRight() != null) {
  //             queue.add(node.getRight());
  //         }

  //     }

  // }
}