package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.home.amazon.iot.model.Item;
import com.home.amazon.iot.util.DependencyFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SubscribeFunction implements RequestHandler<Map<String, String>, String> {

    static final String MESSAGE_KEY = "message";
    static final String LAMBDA_RESPONSE_TEMPLATE = "Subscribe function is invoked, message id: %s";
    static final String LAMBDA_RESPONSE_ERROR_TEMPLATE = "Failed to send message: %s";

    private static final String YAML_FILE_EXTENSION = ".yaml";
    private static final String TMP_FILE_PREFIX = "test-";

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    private final SqsClient sqsClient;
    private final String queueUrl;

    public SubscribeFunction() {
        sqsClient = DependencyFactory.sqsClient();
        queueUrl = DependencyFactory.sqsQueryUrl();
        yamlMapper = new ObjectMapper(new YAMLFactory());
        jsonMapper = new ObjectMapper();
    }

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String response = null;
        if (input != null && input.get(MESSAGE_KEY) != null) {
            try {
                String message = input.get(MESSAGE_KEY);
                String fileName = TMP_FILE_PREFIX + System.currentTimeMillis();
                Path tempFile = Files.createTempFile(fileName, YAML_FILE_EXTENSION);
                Files.write(tempFile, message.getBytes());
                Item item = yamlMapper.readValue(tempFile.toFile(), Item.class);
                String json = jsonMapper.writeValueAsString(item);
                System.out.println("Converted to JSON: " + json);
                response = sendSqsMessage(json);
                System.out.println("Sent SQS message with id: " + response);
            } catch (Exception e) {
                System.out.println("Failed to send SQS message: " + e);
                return String.format(LAMBDA_RESPONSE_ERROR_TEMPLATE, e.getMessage());
            }
        }
        return String.format(LAMBDA_RESPONSE_TEMPLATE, response);
    }

    private String sendSqsMessage(String json) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(json)
                .delaySeconds(5)
                .build();
        SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMsgRequest);
        return sendMessageResponse.messageId();
    }

}
