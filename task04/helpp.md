Here are the complete action steps to accomplish the task of configuring AWS Lambda functions integrated with SQS and SNS using `aws-syndicate` with Java as the runtime:

### Step 1: Install AWS Syndicate
1. Clone or download the AWS Syndicate repository.
   ```bash
   git clone https://github.com/epam/aws-syndicate.git
   cd aws-syndicate
   ```
2. Install the tool following the installation instructions from the [AWS Syndicate GitHub](https://github.com/epam/aws-syndicate/wiki/Installation).

### Step 2: Generate a New Project
Use the `aws-syndicate` tool to generate a new project, which sets up the basic structure for Lambda deployment.
```bash
syndicate generate project my-aws-project
cd my-aws-project
```

### Step 3: Generate Configuration Files
This step will create the necessary configuration files like `syndicate.yml` and `syndicate_aliases.yml`, which can be edited later to suit your specific needs.
```bash
syndicate generate config
```

### Step 4: Generate the Lambda Functions
1. **Generate 'SQS Handler' Lambda Function** (Java Runtime)
   ```bash
   syndicate generate lambda sqs-handler --runtime java11 --function-type sqs-handler
   ```
   This will create the Lambda function skeleton in the project.

2. **Generate 'SNS Handler' Lambda Function** (Java Runtime)
   ```bash
   syndicate generate lambda sns-handler --runtime java11 --function-type sns-handler
   ```

### Step 5: Generate SQS Queue and SNS Topic Resources
1. **Generate SQS Queue Resource**:
   ```bash
   syndicate generate resource sqs my-sqs-queue
   ```

2. **Generate SNS Topic Resource**:
   ```bash
   syndicate generate resource sns my-sns-topic
   ```

### Step 6: Configure Lambda Triggers for SQS and SNS
1. **Configure SQS Queue as a Trigger for 'SQS Handler' Lambda**:
   Modify the `syndicate.yml` file to add the SQS trigger:
   ```yaml
   lambdas:
     sqs-handler:
       handler: com.example.SQSHandler::handleRequest
       runtime: java11
       triggers:
         - sqs: my-sqs-queue
   ```

2. **Configure SNS Topic as a Trigger for 'SNS Handler' Lambda**:
   Modify the `syndicate.yml` file to add the SNS trigger:
   ```yaml
   lambdas:
     sns-handler:
       handler: com.example.SNSHandler::handleRequest
       runtime: java11
       triggers:
         - sns: my-sns-topic
   ```

### Step 7: Implement Lambda Logic
1. In the `SQSHandler.java` file, implement the logic to print the SQS message content to CloudWatch Logs.
   ```java
   public class SQSHandler implements RequestHandler<SQSEvent, Void> {
       @Override
       public Void handleRequest(SQSEvent event, Context context) {
           for (SQSEvent.SQSMessage message : event.getRecords()) {
               System.out.println("Received message: " + message.getBody());
           }
           return null;
       }
   }
   ```

2. In the `SNSHandler.java` file, implement the logic to print the SNS message content to CloudWatch Logs.
   ```java
   public class SNSHandler implements RequestHandler<SNSEvent, Void> {
       @Override
       public Void handleRequest(SNSEvent event, Context context) {
           for (SNSEvent.SNSRecord record : event.getRecords()) {
               System.out.println("Received message: " + record.getSNS().getMessage());
           }
           return null;
       }
   }
   ```

### Step 8: Build and Deploy the Project
1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Deploy using AWS Syndicate**:
   ```bash
   syndicate deploy
   ```

### Step 9: Send Messages to SQS and SNS
1. **Send a message to the SQS queue**:
   You can use the AWS CLI or the AWS Console to send a test message to the SQS queue.
   ```bash
   aws sqs send-message --queue-url <QueueURL> --message-body "Test SQS message"
   ```

2. **Send a message to the SNS topic**:
   Similarly, use the AWS CLI or the AWS Console to send a test message to the SNS topic.
   ```bash
   aws sns publish --topic-arn <TopicArn> --message "Test SNS message"
   ```

### Step 10: Verify Logs in CloudWatch
1. Go to the AWS CloudWatch Console.
2. Check the logs for both the `SQS Handler` and `SNS Handler` Lambda functions.
3. Ensure that the messages sent to the SQS queue and SNS topic are properly logged.

### Step 11: Final Verification
1. Confirm that both Lambda functions (`SQS Handler` and `SNS Handler`) are listed in the AWS Lambda console.
2. Check that there are no errors in deployment.
3. Ensure that the SQS queue and SNS topic are correctly listed and integrated in their respective AWS services.

Following these steps will ensure that your AWS Lambda functions are correctly triggered by SQS and SNS, and the messages are logged to CloudWatch as expected.