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

		if (request instanceof Map) {
			Map<String, Object> event = (Map<String, Object>) request;

			if (event.containsKey("Records")) {
				List<Map<String, Object>> records = (List<Map<String, Object>>) event.get("Records");

				for (Map<String, Object> record : records) {
					Map<String, Object> sns = (Map<String, Object>) record.get("Sns");
					String message = (String) sns.get("Message");

					context.getLogger().log("Received SNS message: " + message);
				}
			} else {
				context.getLogger().log("No SNS Records found in the event.");
			}
		} else {
			context.getLogger().log("Invalid event format: Expected a Map but received " + request.getClass().getName());
		}

		return Map.of("status", "success", "message", "SNS message processed successfully");

	}
}
