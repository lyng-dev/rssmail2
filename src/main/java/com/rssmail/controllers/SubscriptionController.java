package com.rssmail.controllers;

import javax.servlet.http.HttpServletRequest;

import com.rssmail.models.CreateSubscriptionFormData;
import com.rssmail.models.DeleteSubscriptionFormData;
import com.rssmail.models.ValidateSubscriptionFormData;
import com.rssmail.scheduler.RssMailScheduler;
import com.rssmail.services.SubscriptionService.SubscriptionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "subscription")
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private RssMailScheduler scheduler;

    public SubscriptionController(SubscriptionService subscriptionService, RssMailScheduler scheduler) {
        this.subscriptionService = subscriptionService;
        this.scheduler = scheduler;
    }

    @PostMapping(value = "/subscribe")
    public ResponseEntity<String> createSubscription(@RequestBody CreateSubscriptionFormData formData) 
    {
        var newSubscriptionId = subscriptionService.createSubscription(formData.feedUrl(), formData.recipientEmail());
        return ResponseEntity.ok(newSubscriptionId);
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteSubscription(
        @RequestBody DeleteSubscriptionFormData formData) 
    {
        var result = subscriptionService.deleteSubscription(formData.subscriptionId(), formData.recipientEmail());
        if (result) return ResponseEntity.ok("ok");
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
