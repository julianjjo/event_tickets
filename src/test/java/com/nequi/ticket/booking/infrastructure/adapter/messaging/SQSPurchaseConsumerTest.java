package com.nequi.ticket.booking.infrastructure.adapter.messaging;

import com.nequi.ticket.booking.application.usecase.ProcessPurchaseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSPurchaseConsumerTest {

        @Mock
        private SqsAsyncClient sqsClient;

        @Mock
        private ProcessPurchaseUseCase processPurchaseUseCase;

        private SQSPurchaseConsumer consumer;

        @BeforeEach
        void setUp() {
                consumer = new SQSPurchaseConsumer(sqsClient, processPurchaseUseCase);
        }

        @Test
        void shouldStartConsuming() throws Exception {
                GetQueueUrlResponse urlResponse = GetQueueUrlResponse.builder()
                                .queueUrl("http://localhost:4566/queue/purchase-requests")
                                .build();
                when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(urlResponse));

                Message message = Message.builder().body("order-1").receiptHandle("handle-1").build();
                ReceiveMessageResponse receiveResponse = ReceiveMessageResponse.builder()
                                .messages(List.of(message))
                                .build();
                when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(receiveResponse));

                when(processPurchaseUseCase.execute("order-1")).thenReturn(Mono.empty());

                DeleteMessageResponse deleteResponse = DeleteMessageResponse.builder().build();
                when(sqsClient.deleteMessage(any(DeleteMessageRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(deleteResponse));

                consumer.startConsuming();
                Thread.sleep(500);
                consumer.destroy();

                verify(sqsClient).getQueueUrl(any(GetQueueUrlRequest.class));
        }

        @Test
        void shouldHandleProcessingError() throws Exception {
                GetQueueUrlResponse urlResponse = GetQueueUrlResponse.builder()
                                .queueUrl("http://localhost:4566/queue/purchase-requests")
                                .build();
                when(sqsClient.getQueueUrl(any(GetQueueUrlRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(urlResponse));

                Message message = Message.builder().body("order-err").receiptHandle("handle-err").build();
                ReceiveMessageResponse receiveResponse = ReceiveMessageResponse.builder()
                                .messages(List.of(message))
                                .build();
                when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(receiveResponse));

                when(processPurchaseUseCase.execute("order-err"))
                                .thenReturn(Mono.error(new RuntimeException("Processing failed")));

                consumer.startConsuming();
                Thread.sleep(500);
                consumer.destroy();

                verify(sqsClient).getQueueUrl(any(GetQueueUrlRequest.class));
        }
}
