package com.home.amazon.iot.util;

import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public final class DependencyFactory {

    private static final String SQS_QUEUE_URL_ENV_NAME = "QueueUrl";

    private DependencyFactory() {}

    public static SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    public static String sqsQueryUrl() {
        return System.getenv(SQS_QUEUE_URL_ENV_NAME);
    }
}
