package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class SqsHandler implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		Map<String, Object> resultMap = Map.of("status", "success", "message", "SQS messages processed successfully");

		context.getLogger().log("SQS Handler invoked");

		if (request instanceof Map) {
			Map<String, Object> event = (Map<String, Object>) request;

			if (event.containsKey("Records")) {
				List<Map<String, Object>> records = (List<Map<String, Object>>) event.get("Records");

				for (Map<String, Object> record : records) {
					// Extract the body of the SQS message
					String message = (String) record.get("body");

					context.getLogger().log("Received SQS message: " + message);
				}
			} else {
				context.getLogger().log("No SQS Records found in the event.");
				resultMap = Map.of("status", "error", "message", "No SQS records found");
			}
		} else {
			context.getLogger().log("Invalid event format: Expected a Map but received " + request.getClass().getName());
			resultMap = Map.of("status", "error", "message", "Invalid event format");
		}

		return resultMap;


//		System.out.println("Hello from lambda");
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("statusCode", 200);
//		resultMap.put("body", "Hello from Lambda");
//		return resultMap;
	}
}
