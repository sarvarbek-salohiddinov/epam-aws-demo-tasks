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


		Map<String, Object> resultMap = new HashMap<>();

		try {
			// Cast the incoming Object to a Map
			@SuppressWarnings("unchecked")
			Map<String, Object> requestMap = (Map<String, Object>) request;

			// Extract rawPath and method from requestContext
			String rawPath = (String) requestMap.get("rawPath");
			@SuppressWarnings("unchecked")
			Map<String, Object> requestContext = (Map<String, Object>) requestMap.get("requestContext");

			@SuppressWarnings("unchecked")
			Map<String, Object> httpContext = (Map<String, Object>) requestContext.get("http");
			String method = (String) httpContext.get("method");

			// Check if the path is /hello and method is GET
			if ("/hello".equals(rawPath) && "GET".equalsIgnoreCase(method)) {
				resultMap.put("statusCode", 200);
				resultMap.put("message", "Hello from Lambda");
			} else {
				// Return 400 error for all other paths or methods
				resultMap.put("statusCode", 400);
				resultMap.put("message", String.format(
						"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", rawPath, method));
			}
			System.out.println(resultMap);
			System.out.println("=======================================");
			System.out.println("requestContext::   " + requestContext);
			System.out.println("=======================================");
			System.out.println("method::   " + method);
			System.out.println("=======================================");
		} catch (Exception e) {
			// Handle any unexpected errors in processing
			resultMap.put("statusCode", 500);
			resultMap.put("message", "Internal Server Error: " + e.getMessage());
		}



		return resultMap;
	}
}
