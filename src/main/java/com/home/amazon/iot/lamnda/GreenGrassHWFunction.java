package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.home.amazon.iot.task.HelloWorldTask;

import java.util.Timer;

public class GreenGrassHWFunction implements RequestHandler<Object, String> {

    static {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new HelloWorldTask(), 0, 15000);
    }


    @Override
    public String handleRequest(Object input, Context context) {
        return "Hello from Greengrass HW function";
    }
}
