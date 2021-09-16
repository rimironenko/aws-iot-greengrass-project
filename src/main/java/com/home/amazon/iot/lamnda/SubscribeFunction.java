package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.home.amazon.iot.model.Item;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SubscribeFunction implements RequestHandler<Map<String, String>, String> {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private final SqsClient sqsClient;
    private final String queueUrl;

    public SubscribeFunction() {
        sqsClient = SqsClient.builder()
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
        queueUrl = System.getenv("QueueUrl");
    }

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        if (input != null && input.get("message") != null) {
            try {
                String message = input.get("message");
                String fileName = "test-" + System.currentTimeMillis();
                Path tempFile = Files.createTempFile(fileName, ".yaml");
                Files.write(tempFile, message.getBytes());
                Item item = YAML_MAPPER.readValue(tempFile.toFile(), Item.class);
                String json = JSON_MAPPER.writeValueAsString(item);
                System.out.println("Converted to JSON: " + json);
                sendSqsMessage(json);
            } catch (IOException e) {
                System.out.println("Failed to save message: " + e);
            }
        }
        return "Subscribe function is invoked";
    }

    private void sendSqsMessage(String json) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(json)
                .delaySeconds(5)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }

}
