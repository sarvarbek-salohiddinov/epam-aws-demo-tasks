package com.task06;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
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
		lambdaName = "audit_producer",
		roleName = "audit_producer-role",
		aliasName = "learn",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED)
@DependsOn(name = "Configuration", resourceType = ResourceType.DYNAMODB_TABLE)
@DependsOn(name = "Audit", resourceType = ResourceType.DYNAMODB_TABLE)
@DynamoDbTriggerEventSource(
		targetTable = "Configuration",
		batchSize = 10)
@EnvironmentVariables(
		value = {@EnvironmentVariable(key = "region", value = "${region}"),
				 @EnvironmentVariable(key = "target_table", value = "${target_table}"),})
public class AuditProducer implements RequestHandler<Object, Map<String, Object>> {
	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	DynamoDB dynamoDB = new DynamoDB(client);

	public Map<String, Object> handleRequest(Object request, Context context) {
		LambdaLogger logger = context.getLogger();
		DynamodbEvent req = (DynamodbEvent)request;

		logger.log("Request: " + req);
		logger.log("Region: " + System.getenv("region"));
		logger.log("Target table: " + System.getenv("target_table"));
		logger.log("Config table: " + System.getenv("config_table"));

//		Table auditTable = dynamoDB.getTable(System.getenv("target_table"));
		Table auditTable = dynamoDB.getTable("Audit");

		req.getRecords().forEach(record -> {
			if (record.getEventName().equalsIgnoreCase("INSERT"))
				handleInsert(record.getDynamodb().getNewImage(), auditTable, logger);
			else if (record.getEventName().equalsIgnoreCase("MODIFY"))
				handleModify(record.getDynamodb().getOldImage(), record.getDynamodb().getNewImage(), auditTable, logger);
		});

		System.out.println("Hello from lambda");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "Hello from Lambda");
		return resultMap;
	}

	private void handleInsert(Map<String, AttributeValue> newImage,
							  Table auditTable,
							  LambdaLogger logger) {
		String key = newImage.get("key").getS();
		int value = Integer.parseInt(newImage.get("value").getN());

		Item auditItem = new Item()
								.withPrimaryKey("id", UUID.randomUUID().toString())
								.withString("itemKey", key)
								.withString("modificationTime", Instant.now().toString())
								.withMap("newValue", Map.of("key", key, "value", value));

		auditTable.putItem(auditItem);
		logger.log("Inserted audit item: " + auditItem.toJSON());
	}

	private void handleModify(Map<String, AttributeValue> oldImage,
							  Map<String, AttributeValue> newImage,
							  Table auditTable, LambdaLogger logger) {
		String key = newImage.get("key").getS();
		int newValue = Integer.parseInt(newImage.get("value").getN());
		int oldValue = Integer.parseInt(oldImage.get("value").getN());

		Item auditItem = new Item()
								.withPrimaryKey("id", UUID.randomUUID().toString())
								.withString("itemKey", key)
								.withString("modificationTime", Instant.now().toString())
								.withString("updatedAttribute", "value")
								.withInt("oldValue", oldValue)
								.withInt("newValue", newValue);

		auditTable.putItem(auditItem);
		logger.log("Modified audit item: " + auditItem.toJSON());
	}
}
