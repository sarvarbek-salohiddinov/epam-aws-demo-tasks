package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@SqsTriggerEventSource(targetQueue = "async_queue",
					   batchSize = 1)
@DependsOn(name = "async_queue",
		   resourceType = ResourceType.SQS_QUEUE)
@LambdaHandler(lambdaName = "sqs_handler",
			   roleName = "sqs_handler-role",
			   aliasName = "learn",
			   logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED)
public class SqsHandler implements RequestHandler<Object, Map<String, Object>> {
	public Map<String, Object> handleRequest(Object request, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log(request.toString());
		System.out.println("Hello from lambda");
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "Hello from Lambda");
		return resultMap;
	}
}

//	public Map<String, Object> handleRequest(Object request, Context context) {
//		Map<String, Object> resultMap = Map.of("status", "success", "message", "SQS messages processed successfully");
//
//		context.getLogger().log("SQS Handler invoked");
//
//		if (request instanceof Map) {
//			Map<String, Object> event = (Map<String, Object>) request;
//
//			if (event.containsKey("Records")) {
//				List<Map<String, Object>> records = (List<Map<String, Object>>) event.get("Records");
//
//				for (Map<String, Object> record : records) {
//					String message = (String) record.get("body");
//
//					context.getLogger().log("Received SQS message: " + message);
//				}
//			} else {
//				context.getLogger().log("No SQS Records found in the event.");
//				resultMap = Map.of("status", "error", "message", "No SQS records found");
//			}
//		} else {
//			context.getLogger().log("Invalid event format: Expected a Map but received " + request.getClass().getName());
//			resultMap = Map.of("status", "error", "message", "Invalid event format");
//		}
//		System.out.println(resultMap);
//
//		return resultMap;
//
//
////		System.out.println("Hello from lambda");
////		Map<String, Object> resultMap = new HashMap<String, Object>();
////		resultMap.put("statusCode", 200);
////		resultMap.put("body", "Hello from Lambda");
////		return resultMap;
//	}

