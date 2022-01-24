package com.rssmail.controllers;

import com.rssmail.models.CheckFeedFormData;
import com.rssmail.models.CreateSubscriptionFormData;
import com.rssmail.models.DeleteSubscriptionFormData;
import com.rssmail.models.ValidateSubscriptionFormData;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.RssService.RssService;
import com.rssmail.services.SubscriptionService.SubscriptionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "subscription")
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private RssMailScheduler scheduler;
    private RssService rssService;

    public SubscriptionController(SubscriptionService subscriptionService, RssMailScheduler scheduler, RssService rssService) {
        this.subscriptionService = subscriptionService;
        this.scheduler = scheduler;
        this.rssService = rssService;
    }

    //todo: instead return json with validity state inside, and always return 200
    @PostMapping(value = "/checkfeed")
    public ResponseEntity<String> checkFeed(@RequestBody CheckFeedFormData formData) {
        var isValidFeed = rssService.validateFeed(formData.feedUrl());
        if (isValidFeed) return ResponseEntity.ok("ok");
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteSubscription(@RequestBody DeleteSubscriptionFormData formData) {
        var deletedSubscription = subscriptionService.deleteSubscription(formData.subscriptionId(), formData.recipientEmail());
        if (deletedSubscription) {
            scheduler.stop(formData.subscriptionId());
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/create")
    public ResponseEntity<String> createSubscription(@RequestBody CreateSubscriptionFormData formData) 
    {
        var feedUrl = formData.feedUrl();
        var feedIsValid = rssService.validateFeed(feedUrl);
        if (feedIsValid) {
            var newSubscriptionId = subscriptionService.createSubscription(formData.feedUrl(), formData.recipientEmail());
            return ResponseEntity.ok(newSubscriptionId);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/validate")
    @ResponseBody
    public ResponseEntity<String> validateSubscription(@RequestBody ValidateSubscriptionFormData formData) 
    {
        var subscriptionId = formData.subscriptionId();
        var validationCode = formData.validationCode();
        System.out.println(subscriptionId);
        System.out.println(validationCode);
        var result = subscriptionService.validateSubscription(subscriptionId, validationCode);
        if (result) {
            try {
                var subscription = subscriptionService.getSubscription(subscriptionId);
                scheduler.start(subscription);
                return ResponseEntity.ok("ok");
            } 
            catch (Exception e) {
                System.out.println("Something bad happened in SubscriptionController.validateSubscription");
            }
        }
        return ResponseEntity.badRequest().build();
    }

}
