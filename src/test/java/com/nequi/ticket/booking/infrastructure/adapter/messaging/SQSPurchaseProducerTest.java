package com.nequi.ticket.booking.infrastructure.adapter.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSPurchaseProducerTest {

    @Mock
    private SqsAsyncClient sqsClient;

    private SQSPurchaseProducer producer;

    @BeforeEach
    void setUp() {
        producer = new SQSPurchaseProducer(sqsClient);
    }

    @Test
    void shouldPublishPurchaseRequest() {
        GetQueueUrlResponse urlResponse = GetQueueUrlResponse.builder()
                .queueUrl("http://localhost:4566/queue/purchase-requests")
                .build();
        when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(urlResponse));

        SendMessageResponse sendResponse = SendMessageResponse.builder().messageId("msg-1").build();
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResponse));

        StepVerifier.create(producer.publishPurchaseRequest("order-1"))
                .verifyComplete();
    }
}
