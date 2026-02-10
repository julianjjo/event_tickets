package com.nequi.ticket.booking.infrastructure.adapter.messaging;

import com.nequi.ticket.booking.domain.port.MessagePublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SQSPurchaseProducer implements MessagePublisher {

    private final SqsAsyncClient sqsClient;
    private static final String QUEUE_NAME = "purchase-requests";

    public SQSPurchaseProducer(SqsAsyncClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public Mono<Void> publishPurchaseRequest(String orderId) {
        return Mono
                .fromCompletionStage(sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build()))
                .flatMap(urlResponse -> {
                    SendMessageRequest request = SendMessageRequest.builder()
                            .queueUrl(urlResponse.queueUrl())
                            .messageBody(orderId)
                            .build();
                    return Mono.fromCompletionStage(sqsClient.sendMessage(request));
                })
                .then();
    }
}
