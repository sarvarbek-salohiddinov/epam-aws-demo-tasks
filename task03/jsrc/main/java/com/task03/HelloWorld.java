package com.task03;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
//		Map<String, Object> resultMap = new LinkedHashMap<>();
//
//		try {
//			@SuppressWarnings("unchecked")
//			Map<String, Object> requestMap = (Map<String, Object>) request;
//
//			String rawPath = (String) requestMap.get("rawPath");
//
//			@SuppressWarnings("unchecked")
//			Map<String, Object> requestContext = (Map<String, Object>) requestMap.get("requestContext");
//
//			@SuppressWarnings("unchecked")
//			Map<String, Object> httpContext = (Map<String, Object>) requestContext.get("http");
//
//			String method = (String) httpContext.get("method");
//
//			if ("/hello".equals(rawPath) && "GET".equalsIgnoreCase(method)) {
//				resultMap.put("statusCode", 200);
//				resultMap.put("body", "{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}");
//			} else {
//				resultMap.put("statusCode", 400);
//				resultMap.put("body", "{\"statusCode\": 400, \"message\": \"" + String.format(
//						"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", rawPath, method) + "\"}");
//			}
//		} catch (Exception e) {
//			resultMap.put("statusCode", 500);
//			resultMap.put("body", "{\"statusCode\": 500, message\": {Internal Server Error: " + e.getMessage() + "\"}");
//		}
//		return resultMap;
		System.out.println("Hello from lambda");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
		resultMap.put("message", "Hello from Lambda");
		return resultMap;
	}
}
