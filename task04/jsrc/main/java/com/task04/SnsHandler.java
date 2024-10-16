package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class SnsHandler implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		context.getLogger().log("SNS Handler invoked");

		// Check if the request is of type Map (expected SNS event format)
		if (request instanceof Map) {
			Map<String, Object> event = (Map<String, Object>) request;

			// Check if the "Records" key is present in the event
			if (event.containsKey("Records")) {
				List<Map<String, Object>> records = (List<Map<String, Object>>) event.get("Records");

				for (Map<String, Object> record : records) {
					// Extract the SNS part of the record
					Map<String, Object> sns = (Map<String, Object>) record.get("Sns");
					String message = (String) sns.get("Message");

					// Log the received SNS message
					context.getLogger().log("Received SNS message: " + message);
				}
			} else {
				// Log an error if no "Records" key found
				context.getLogger().log("No SNS Records found in the event.");
			}
		} else {
			// Log an error if the request is not a map
			context.getLogger().log("Invalid event format: Expected a Map but received " + request.getClass().getName());
		}

		// Return a simple success message in the response map
		return Map.of("status", "success", "message", "SNS message processed successfully");

	}
}
