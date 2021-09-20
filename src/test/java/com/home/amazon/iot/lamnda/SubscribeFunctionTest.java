package com.home.amazon.iot.lamnda;

import com.amazonaws.services.lambda.runtime.Context;
import com.home.amazon.iot.util.DependencyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscribeFunctionTest {

    private static final String TEST_QUEUE_URL = "https://test-queue.us-east-1.amazonaws.com";
    private static final String TEST_MESSAGE_ID = "ab-1";
    private static final String EXCEPTION_MESSAGE = "Exception";

    @Mock
    private SqsClient sqsClient;

    @Mock
    private Context context;

    @Mock
    private SendMessageResponse expectedResponse;

    @Test
    public void shouldSendSqsMessageIfExecutionWasCorrect() throws IOException {
        Map<String, String> testEvent = new HashMap<>();
        testEvent.put(SubscribeFunction.MESSAGE_KEY, new String(Files.readAllBytes(Paths.get("src/test/resources/test.yaml"))));
        when(expectedResponse.messageId()).thenReturn(TEST_MESSAGE_ID);
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(expectedResponse);
        try (MockedStatic<DependencyFactory> mockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.sqsClient()).thenReturn(sqsClient);
            when(DependencyFactory.sqsQueryUrl()).thenReturn(TEST_QUEUE_URL);
            SubscribeFunction subscribeFunction = new SubscribeFunction();
            String actualResponse = subscribeFunction.handleRequest(testEvent, context);
            assertEquals(String.format(SubscribeFunction.LAMBDA_RESPONSE_TEMPLATE, TEST_MESSAGE_ID), actualResponse);
        }
    }

    @Test
    public void shouldReturnErrorMessageWhenIOExceptionOccurs() throws IOException {
        Map<String, String> testEvent = new HashMap<>();
        testEvent.put(SubscribeFunction.MESSAGE_KEY, new String(Files.readAllBytes(Paths.get("src/test/resources/test.yaml"))));
        try (MockedStatic<DependencyFactory> mockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.sqsClient()).thenReturn(sqsClient);
            when(DependencyFactory.sqsQueryUrl()).thenReturn(TEST_QUEUE_URL);
            SubscribeFunction subscribeFunction = new SubscribeFunction();
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                when(Files.createTempFile(anyString(), anyString())).thenThrow(new IOException(EXCEPTION_MESSAGE));
                String response = subscribeFunction.handleRequest(testEvent, context);
                assertEquals(String.format(SubscribeFunction.LAMBDA_RESPONSE_ERROR_TEMPLATE, EXCEPTION_MESSAGE), response);
            }

        }
    }

    @Test
    public void shouldReturnMessageWithNullIfNoMessage() {
        try (MockedStatic<DependencyFactory> mockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.sqsClient()).thenReturn(sqsClient);
            when(DependencyFactory.sqsQueryUrl()).thenReturn(TEST_QUEUE_URL);
            SubscribeFunction subscribeFunction = new SubscribeFunction();
            String response = subscribeFunction.handleRequest(new HashMap<>(), context);
            assertEquals(String.format(SubscribeFunction.LAMBDA_RESPONSE_TEMPLATE, (String) null), response);
        }
    }

}