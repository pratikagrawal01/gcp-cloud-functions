package com.costco.cloudrun.publisher;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

@Service
public class PubSubPublisher {

	private final String topicId = "bf-webhook"; // Replace with your Pub/Sub topic name

	public String publishMessage(@RequestBody String message) {
		String projectId = "primordial-veld-442819-k2"; // Replace with your GCP project ID
		String topicName = String.format("projects/%s/topics/%s", projectId, topicId);

		Publisher publisher = null;
		try {
			publisher = Publisher.newBuilder(topicName).build();

			ByteString data = ByteString.copyFromUtf8(message);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
					.setData(data)
					.build();

			publisher.publish(pubsubMessage).get(); // Synchronously publish the message
			return "Message published successfully: " + message;

		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to publish message";
		} finally {
			if (publisher != null) {
				try {
					publisher.shutdown();
					publisher.awaitTermination(1, TimeUnit.MINUTES);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
