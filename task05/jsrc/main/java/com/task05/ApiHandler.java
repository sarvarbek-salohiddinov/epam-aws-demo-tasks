package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
@DynamoDbTriggerEventSource(
		targetTable = "Events",
		batchSize = 10
)
@DependsOn(
		resourceType = ResourceType.DYNAMODB_TABLE,
		name = "Events"
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "region", value = "${region}"),
		@EnvironmentVariable(key = "target_table", value = "${target_table}")
})
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		@SuppressWarnings("unchecked")
        Map<String, Object> requestToUse = (Map<String, Object>) request;


		System.out.println("Hello from lambda");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "Hello from Lambda");
		return resultMap;
	}
}
