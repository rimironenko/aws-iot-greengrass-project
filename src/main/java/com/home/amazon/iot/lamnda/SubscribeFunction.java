package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class SubscribeFunction implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        System.out.println(input);
        return "Subscribe function is invoked";
    }

}
