package com.costco.cloudrun.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.costco.cloudrun.publisher.PubSubPublisher;

@RestController
public class WebHookListner {

	@Autowired
	PubSubPublisher pubSubPublisher;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PostMapping("/publish")
	public String publishMessage(@RequestBody String message) {

		try {
			logger.info("Webhook received for : {}", message);
			return pubSubPublisher.publishMessage(message);
		} catch (Exception e) {
			logger.info("Pubsub failed for webhook : {}", message);
			return "Failed to publish message";
		} 
	}

}
