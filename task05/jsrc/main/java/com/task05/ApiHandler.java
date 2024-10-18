package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role",
		isPublishVersion = true,
		aliasName = "learn",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED)
@DynamoDbTriggerEventSource(
		targetTable = "Events",
		batchSize = 10)
@DependsOn(
		resourceType = ResourceType.DYNAMODB_TABLE,
		name = "Events")
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "region", value = "${region}"),
		@EnvironmentVariable(key = "target_table", value = "${target_table}")})
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {
	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

	public Map<String, Object> handleRequest(Object request, Context context) {
		@SuppressWarnings("unchecked")
        Map<String, Object> requestToUse = (Map<String, Object>) request;
		LambdaLogger logger = context.getLogger();
		logger.log("Incoming request: " + request.toString());

		Map<String, Object> resultMap = new HashMap<>();

		String tableName = System.getenv("target_table");
		logger.log("Table name: " + tableName);

		String createdTime = Instant.now().toString();
		logger.log("Created time: " + createdTime);


		try {
			String id = UUID.randomUUID().toString();
			int principalId = Integer.parseInt(requestToUse.get("principalId").toString());
			@SuppressWarnings("unchecked")
			Map<String, String> content = (Map<String, String>) requestToUse.get("content");

			Map<String, AttributeValue> item = new HashMap<>();
			item.put("id", new AttributeValue().withS(id));
			item.put("principalId", new AttributeValue().withN(String.valueOf(principalId)));
			item.put("createdAt", new AttributeValue().withS(createdTime));
			item.put("body", new AttributeValue().withM(convertContentMap(content)));

			logger.log("item: " + item);
			PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
			PutItemResult itemResult = client.putItem(itemRequest);
			logger.log("Put Item Result: " + itemResult);

			Map<String, Object> eventResp = new HashMap<>();
			eventResp.put("id", id);
			eventResp.put("principalId", principalId);
			eventResp.put("createdAt", createdTime);
			eventResp.put("body", content);

			resultMap.put("statusCode", 201);
			resultMap.put("event", eventResp);

			return resultMap;
		} catch (Exception e) {
			context.getLogger().log("Error: " + e.getMessage());
			resultMap.put("statusCode", 500);
			context.getLogger().log("Error: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private Map<String, AttributeValue> convertContentMap(Map<String, String> content) {
		Map<String, AttributeValue> contentMap = new HashMap<>();
		for (Map.Entry<String, String> entry : content.entrySet())
			contentMap.put(entry.getKey(), new AttributeValue(entry.getValue()));
		return contentMap;
	}
}
