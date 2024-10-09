package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;


@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED)
@LambdaUrlConfig(
		authType= AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
//		Map<String, Object> resultMap = new HashMap<>();
//
//		System.out.println("Received request: " + request.toString());
//
//		@SuppressWarnings("unchecked")
//		Map<String, Object> requestMap = (Map<String, Object>) request;
//
//		String path = (String) requestMap.get("rawPath");
//		String method = (String) requestMap.get("httpMethod");
//
//		if ("/hello".equals(path) && "GET".equalsIgnoreCase(method)) {
//			resultMap.put("statusCode", 200);
//			resultMap.put("message", "Hello from Lambda");
//		} else {
//			resultMap.put("statusCode", 400);
//			resultMap.put("message", String.format(
//					"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method));
//		}
//
//		return resultMap;

		Map<String, Object> parsedMap = parseObjectToMap(request);

		// Print the parsed Map
		System.out.println(request);
		System.out.println("================================================");
		System.out.println(parsedMap);
		System.out.println("================================================");
		System.out.println(parsedMap.get("requestContext"));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
//		resultMap.put("message", "Hello from Lambda");
		resultMap.put("message", request);
		return resultMap;
	}


	public static Map<String, Object> parseObjectToMap(Object request) {
		String requestString = request.toString();
		Map<String, Object> resultMap = new HashMap<>();
		parseKeyValuePairs(requestString, resultMap);

		return resultMap;
	}

	private static void parseKeyValuePairs(String input, Map<String, Object> resultMap) {
		input = input.replaceAll("[{}]", "");
		String[] pairs = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Comma outside quotes

		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2); // Split only at the first '='

			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				if (value.startsWith("{") && value.endsWith("}")) {
					Map<String, Object> nestedMap = new HashMap<>();
					parseKeyValuePairs(value, nestedMap);
					resultMap.put(key, nestedMap);
				} else resultMap.put(key, value);

			}
		}
	}
}
