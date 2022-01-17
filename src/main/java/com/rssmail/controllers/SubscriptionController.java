package com.rssmail.controllers;

import com.rssmail.services.SubscriptionService.SubscriptionService;

import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<String> createSubscription(String feedUrl, String recipientEmail) {
        subscriptionService.createSubscription(feedUrl, recipientEmail);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteSubscription() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity<String> validateSubscription(String subscriptionId, String validationCode) {
        subscriptionService.validateSubscription(subscriptionId, validationCode);
        return ResponseEntity.ok("ok");
    }

}
