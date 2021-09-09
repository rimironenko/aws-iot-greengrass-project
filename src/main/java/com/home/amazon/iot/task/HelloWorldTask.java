package com.home.amazon.iot.task;

import com.amazonaws.greengrass.javasdk.IotDataClient;
import com.amazonaws.greengrass.javasdk.model.PublishRequest;
import com.amazonaws.greengrass.javasdk.model.QueueFullPolicy;

import java.nio.ByteBuffer;
import java.util.TimerTask;

public class HelloWorldTask extends TimerTask {

    private IotDataClient iotDataClient = new IotDataClient();
    private String publishMessage = String.format("Hello world! Sent from Greengrass Core running on platform: %s-%s using Java", System.getProperty("os.name"), System.getProperty("os.version"));
    private PublishRequest publishRequest = new PublishRequest()
            .withTopic("java/hello/world")
            .withPayload(ByteBuffer.wrap(String.format("{\"message\":\"%s\"}", publishMessage).getBytes()))
            .withQueueFullPolicy(QueueFullPolicy.AllOrException);

    @Override
    public void run() {
        try {
            iotDataClient.publish(publishRequest);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
