package com.nequi.ticket.booking.infrastructure.adapter.persistence;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class DynamoDBEventRepository implements EventRepository {

        private final DynamoDbAsyncClient client;
        private static final String TABLE_NAME = "Events";

        public DynamoDBEventRepository(DynamoDbAsyncClient client) {
                this.client = client;
        }

        @Override
        public Mono<Event> findById(String id) {
                GetItemRequest request = GetItemRequest.builder()
                                .tableName(TABLE_NAME)
                                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                                .build();

                return Mono.fromCompletionStage(client.getItem(request))
                                .map(GetItemResponse::item)
                                .filter(item -> !item.isEmpty())
                                .map(this::mapToEvent);
        }

        @Override
        public Flux<Event> findAll() {
                ScanRequest request = ScanRequest.builder().tableName(TABLE_NAME).build();
                return Mono.fromCompletionStage(client.scan(request))
                                .flatMapMany(response -> Flux.fromIterable(response.items()))
                                .map(this::mapToEvent);
        }

        @Override
        public Mono<Event> save(Event event) {
                PutItemRequest request = PutItemRequest.builder()
                                .tableName(TABLE_NAME)
                                .item(Map.of(
                                                "id", AttributeValue.builder().s(event.id()).build(),
                                                "name", AttributeValue.builder().s(event.name()).build(),
                                                "date", AttributeValue.builder().s(event.date().toString()).build(),
                                                "location", AttributeValue.builder().s(event.location()).build(),
                                                "totalCapacity",
                                                AttributeValue.builder().n(String.valueOf(event.totalCapacity()))
                                                                .build(),
                                                "availableTickets",
                                                AttributeValue.builder().n(String.valueOf(event.availableTickets()))
                                                                .build(),
                                                "version",
                                                AttributeValue.builder().n(String.valueOf(event.version())).build()))
                                .build();

                return Mono.fromCompletionStage(client.putItem(request)).thenReturn(event);
        }

        @Override
        public Mono<Event> updateAvailability(String eventId, Integer decrement, Long currentVersion) {
                UpdateItemRequest request = UpdateItemRequest.builder()
                                .tableName(TABLE_NAME)
                                .key(Map.of("id", AttributeValue.builder().s(eventId).build()))
                                .updateExpression(
                                                "SET availableTickets = availableTickets - :dec, version = version + :inc")
                                .conditionExpression("availableTickets >= :dec AND version = :ver")
                                .expressionAttributeValues(Map.of(
                                                ":dec", AttributeValue.builder().n(String.valueOf(decrement)).build(),
                                                ":inc", AttributeValue.builder().n("1").build(),
                                                ":ver",
                                                AttributeValue.builder().n(String.valueOf(currentVersion)).build()))
                                .returnValues(ReturnValue.ALL_NEW)
                                .build();

                return Mono.fromCompletionStage(client.updateItem(request))
                                .map(UpdateItemResponse::attributes)
                                .map(this::mapToEvent);
        }

        private Event mapToEvent(Map<String, AttributeValue> item) {
                return new Event(
                                item.get("id").s(),
                                item.get("name").s(),
                                LocalDateTime.parse(item.get("date").s()),
                                item.get("location").s(),
                                Integer.parseInt(item.get("totalCapacity").n()),
                                Integer.parseInt(item.get("availableTickets").n()),
                                Long.parseLong(item.get("version").n()));
        }
}
