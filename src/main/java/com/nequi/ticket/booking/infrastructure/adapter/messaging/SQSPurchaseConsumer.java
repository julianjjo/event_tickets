package com.nequi.ticket.booking.infrastructure.adapter.messaging;

import com.nequi.ticket.booking.application.usecase.ProcessPurchaseUseCase;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;

@Component
public class SQSPurchaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(SQSPurchaseConsumer.class);
    private final SqsAsyncClient sqsClient;
    private final ProcessPurchaseUseCase processPurchaseUseCase;
    private static final String QUEUE_NAME = "purchase-requests";

    public SQSPurchaseConsumer(SqsAsyncClient sqsClient, ProcessPurchaseUseCase processPurchaseUseCase) {
        this.sqsClient = sqsClient;
        this.processPurchaseUseCase = processPurchaseUseCase;
    }

    @PostConstruct
    public void startConsuming() {
        log.info("Starting SQS consumer...");
        Mono.fromCompletionStage(sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build()))
                .map(urlResponse -> urlResponse.queueUrl())
                .flatMapMany(this::pollMessages)
                .subscribe();
    }

    private Flux<Void> pollMessages(String queueUrl) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .build();

        return Flux.interval(Duration.ZERO, Duration.ofSeconds(1))
                .flatMap(i -> Mono.fromCompletionStage(sqsClient.receiveMessage(receiveRequest)))
                .flatMapIterable(response -> response.messages())
                .flatMap(message -> {
                    String orderId = message.body();
                    return processPurchaseUseCase.execute(orderId)
                            .then(deleteMessage(queueUrl, message.receiptHandle()))
                            .onErrorResume(e -> {
                                log.error("Failed to process message {}: {}", orderId, e.getMessage());
                                return Mono.empty();
                            });
                });
    }

    private Mono<Void> deleteMessage(String queueUrl, String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        return Mono.fromCompletionStage(sqsClient.deleteMessage(deleteRequest)).then();
    }
}
