package com.rssmail.controllers;

import com.rssmail.services.SubscriptionService.SubscriptionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "subscription")
public class SubscriptionController {

    private SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public ResponseEntity<String> createSubscription(
        @ModelAttribute("feedUrl") String feedUrl, 
        @ModelAttribute("recipientEmail") String recipientEmail) 
    {
        subscriptionService.createSubscription(feedUrl, recipientEmail);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteSubscription(
        @ModelAttribute("subscriptionId") String subscriptionId, 
        @ModelAttribute("recipientEmail") String recipientEmail) 
    {
        var result = subscriptionService.deleteSubscription(subscriptionId, recipientEmail);
        if (result) return ResponseEntity.ok("ok");
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity<String> validateSubscription(
        @ModelAttribute("subscriptionId") String subscriptionId, 
        @ModelAttribute("validationCode") String validationCode) 
    {
        var result = subscriptionService.validateSubscription(subscriptionId, validationCode);
        if (result) return ResponseEntity.ok("ok");
        return ResponseEntity.badRequest().build();
    }

}
