/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package functions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// [START functions_pubsub_publish]
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

public class PublishMessage implements HttpFunction {
	// TODO<developer> set this environment variable
	private static final String PROJECT_ID = "primordial-veld-442819-k2";

	private static final Logger logger = Logger.getLogger(PublishMessage.class.getName());

	public void service(HttpRequest request, HttpResponse response) throws IOException {
		String maybeTopicName = "bf-webhook";
		String maybeMessage = "";

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			maybeMessage = reader.lines().collect(Collectors.joining("\n"));           
		} catch (IOException e) {
		}
		 logger.info("Body :"+maybeMessage);

		BufferedWriter responseWriter = response.getWriter();

		if (maybeTopicName.isEmpty() || maybeMessage.isEmpty()) {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
			responseWriter.write("Missing 'topic' and/or 'message' parameter(s).");
			return;
		}


		logger.info("Publishing message to topic: " + maybeTopicName);
		ByteString byteStr = ByteString.copyFrom(maybeMessage, StandardCharsets.UTF_8);
		PubsubMessage pubsubApiMessage = PubsubMessage.newBuilder().setData(byteStr).build();

		Publisher publisher = Publisher.newBuilder(
				ProjectTopicName.of(PROJECT_ID, maybeTopicName)).build();

		// Attempt to publish the message
		String responseMessage;
		try {
			publisher.publish(pubsubApiMessage).get();
			responseMessage = "Message published.";
      logger.info("Message Published");
		} catch (InterruptedException | ExecutionException e) {
			logger.log(Level.SEVERE, "Error publishing Pub/Sub message: " + e.getMessage(), e);
			responseMessage = "Error publishing Pub/Sub message; see logs for more info.";
		}

		responseWriter.write(responseMessage);
	}
}
// [END functions_pubsub_publish]