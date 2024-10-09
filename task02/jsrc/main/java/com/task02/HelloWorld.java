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


		System.out.println("Hello from lambda");
		System.out.println(request);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
//		resultMap.put("message", "Hello from Lambda");
		resultMap.put("message", request);
		return resultMap;
	}
}
