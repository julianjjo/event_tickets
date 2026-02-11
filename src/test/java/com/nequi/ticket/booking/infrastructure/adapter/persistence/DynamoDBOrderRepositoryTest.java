package com.nequi.ticket.booking.infrastructure.adapter.persistence;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDBOrderRepositoryTest {

    @Mock
    private DynamoDbAsyncClient client;

    private DynamoDBOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DynamoDBOrderRepository(client);
    }

    private Map<String, AttributeValue> createOrderItem() {
        return Map.of(
                "id", AttributeValue.builder().s("order-1").build(),
                "eventId", AttributeValue.builder().s("event-1").build(),
                "userId", AttributeValue.builder().s("user-1").build(),
                "quantity", AttributeValue.builder().n("2").build(),
                "status", AttributeValue.builder().s("RESERVED").build(),
                "createdAt", AttributeValue.builder().s(LocalDateTime.of(2026, 1, 1, 10, 0).toString()).build(),
                "expiresAt", AttributeValue.builder().s(LocalDateTime.of(2026, 1, 1, 10, 10).toString()).build());
    }

    @Test
    void shouldFindById() {
        GetItemResponse response = GetItemResponse.builder().item(createOrderItem()).build();
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.findById("order-1"))
                .expectNextMatches(order -> order.id().equals("order-1") && order.status() == TicketStatus.RESERVED)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        GetItemResponse response = GetItemResponse.builder().item(Map.of()).build();
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.findById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void shouldSave() {
        PutItemResponse response = PutItemResponse.builder().build();
        when(client.putItem(any(PutItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Order order = new Order("order-1", "event-1", "user-1", 2, TicketStatus.RESERVED,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        StepVerifier.create(repository.save(order))
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void shouldUpdateStatus() {
        UpdateItemResponse response = UpdateItemResponse.builder().attributes(createOrderItem()).build();
        when(client.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.updateStatus("order-1", TicketStatus.SOLD))
                .expectNextMatches(order -> order.id().equals("order-1"))
                .verifyComplete();
    }

    @Test
    void shouldFindExpiredReservations() {
        ScanResponse response = ScanResponse.builder().items(createOrderItem()).build();
        when(client.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.findExpiredReservations(LocalDateTime.now()))
                .expectNextCount(1)
                .verifyComplete();
    }
}
