package com.home.amazon.iot.lamnda;

import com.amazonaws.greengrass.javasdk.IotDataClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.home.amazon.iot.task.SendDataTask;

import java.util.Timer;

public class CollectDataFunction implements RequestHandler<Object, String> {

    public static final String ENV_MONITORED_PATH = "SendDataTask_path";

    public static final String ENV_SUBSCRIPTION_TOPIC = "SendDataTask_topic";

    static {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new SendDataTask(new IotDataClient(), System.getenv(ENV_MONITORED_PATH), System.getenv(ENV_SUBSCRIPTION_TOPIC)), 0, 15000);
        System.out.println("Data collection task started");
    }

    @Override
    public String handleRequest(Object input, Context context) {
        return "Collect Data Function started";
    }
}
