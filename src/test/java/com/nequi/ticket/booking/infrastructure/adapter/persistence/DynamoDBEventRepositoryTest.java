package com.nequi.ticket.booking.infrastructure.adapter.persistence;

import com.nequi.ticket.booking.domain.model.Event;
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
class DynamoDBEventRepositoryTest {

    @Mock
    private DynamoDbAsyncClient client;

    private DynamoDBEventRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DynamoDBEventRepository(client);
    }

    private Map<String, AttributeValue> createEventItem() {
        return Map.of(
                "id", AttributeValue.builder().s("event-1").build(),
                "name", AttributeValue.builder().s("Concert").build(),
                "date", AttributeValue.builder().s(LocalDateTime.of(2026, 3, 1, 20, 0).toString()).build(),
                "location", AttributeValue.builder().s("Stadium").build(),
                "totalCapacity", AttributeValue.builder().n("100").build(),
                "availableTickets", AttributeValue.builder().n("80").build(),
                "version", AttributeValue.builder().n("1").build());
    }

    @Test
    void shouldFindById() {
        GetItemResponse response = GetItemResponse.builder().item(createEventItem()).build();
        when(client.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.findById("event-1"))
                .expectNextMatches(event -> event.id().equals("event-1") && event.name().equals("Concert"))
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
    void shouldFindAll() {
        ScanResponse response = ScanResponse.builder().items(createEventItem()).build();
        when(client.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.findAll())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldSave() {
        PutItemResponse response = PutItemResponse.builder().build();
        when(client.putItem(any(PutItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Event event = new Event("event-1", "Concert", LocalDateTime.now(), "Stadium", 100, 100, 0L);

        StepVerifier.create(repository.save(event))
                .expectNext(event)
                .verifyComplete();
    }

    @Test
    void shouldUpdateAvailability() {
        UpdateItemResponse response = UpdateItemResponse.builder().attributes(createEventItem()).build();
        when(client.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(repository.updateAvailability("event-1", 5, 0L))
                .expectNextMatches(event -> event.id().equals("event-1"))
                .verifyComplete();
    }
}
