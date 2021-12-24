package com.rssmail.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "subscription")
public class SubscriptionController {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<String> createSubscription() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteSubscription() {
        return ResponseEntity.ok("ok");
    }

}
