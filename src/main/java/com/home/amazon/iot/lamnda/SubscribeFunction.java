package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.home.amazon.iot.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SubscribeFunction implements RequestHandler<Map<String, String>, String> {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        if (input != null && input.get("message") != null) {
            try {
                String message = input.get("message");
                String fileName = "test-" + System.currentTimeMillis();
                Path tempFile = Files.createTempFile(fileName, ".yaml");
                System.out.println("Created temp file: " + tempFile);
                Files.write(tempFile, message.getBytes());
                Item item = YAML_MAPPER.readValue(tempFile.toFile(), Item.class);
                System.out.println(item);
                String json = JSON_MAPPER.writeValueAsString(item);
                System.out.println("Converted to JSON: " + json);
            } catch (IOException e) {
                System.out.println("Failed to save message: " + e);
            }
        }
        return "Subscribe function is invoked";
    }

}
