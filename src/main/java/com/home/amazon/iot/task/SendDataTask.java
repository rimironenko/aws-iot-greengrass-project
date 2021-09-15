package com.home.amazon.iot.task;

import com.amazonaws.greengrass.javasdk.IotDataClient;
import com.amazonaws.greengrass.javasdk.model.GGIotDataException;
import com.amazonaws.greengrass.javasdk.model.GGLambdaException;
import com.amazonaws.greengrass.javasdk.model.PublishRequest;
import com.amazonaws.greengrass.javasdk.model.QueueFullPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.home.amazon.iot.model.Item;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SendDataTask extends TimerTask {

    private final IotDataClient iotDataClient;
    private final String monitoredPath;
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private final String topic;

    public SendDataTask(IotDataClient iotDataClient, String monitoredPath, String topic) {
        this.iotDataClient = iotDataClient;
        this.monitoredPath = monitoredPath;
        this.topic = topic;
        yamlMapper = new ObjectMapper(new YAMLFactory());
        jsonMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            PublishRequest publishRequest = new PublishRequest()
                    .withTopic(topic)
                    .withPayload(ByteBuffer.wrap(transformFileDataToJson(findDataFiles()).getBytes()))
                    .withQueueFullPolicy(QueueFullPolicy.AllOrException);
            iotDataClient.publish(publishRequest);
        } catch (IOException | GGIotDataException | GGLambdaException e) {
            System.err.println(e);
        }
    }

    private Set<Path> findDataFiles() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(monitoredPath))) {
            return stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
        }
    }

    private String transformFileDataToJson(Set<Path> files) throws IOException {
        Set<Item> data = new HashSet<>();
        for (Path path : files) {
            System.out.println("Found file:" + path.getFileName());
            Item item = yamlMapper.readValue(path.toFile(), Item.class);
            data.add(item);
        }
        String json = jsonMapper.writeValueAsString(data);
        System.out.println("Built JSON for message: " + json);
        return json;
    }
}
