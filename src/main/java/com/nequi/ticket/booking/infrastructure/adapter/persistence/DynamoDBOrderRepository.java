package com.nequi.ticket.booking.infrastructure.adapter.persistence;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
public class DynamoDBOrderRepository implements OrderRepository {

    private final DynamoDbAsyncClient client;
    private static final String TABLE_NAME = "Orders";

    public DynamoDBOrderRepository(DynamoDbAsyncClient client) {
        this.client = client;
    }

    @Override
    public Mono<Order> findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();

        return Mono.fromCompletionStage(client.getItem(request))
                .map(GetItemResponse::item)
                .filter(item -> !item.isEmpty())
                .map(this::mapToOrder);
    }

    @Override
    public Mono<Order> save(Order order) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(Map.of(
                        "id", AttributeValue.builder().s(order.id()).build(),
                        "eventId", AttributeValue.builder().s(order.eventId()).build(),
                        "userId", AttributeValue.builder().s(order.userId()).build(),
                        "quantity", AttributeValue.builder().n(String.valueOf(order.quantity())).build(),
                        "status", AttributeValue.builder().s(order.status().name()).build(),
                        "createdAt", AttributeValue.builder().s(order.createdAt().toString()).build(),
                        "expiresAt", AttributeValue.builder().s(order.expiresAt().toString()).build()))
                .build();

        return Mono.fromCompletionStage(client.putItem(request)).thenReturn(order);
    }

    @Override
    public Mono<Order> updateStatus(String orderId, TicketStatus status) {
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("id", AttributeValue.builder().s(orderId).build()))
                .updateExpression("SET #s = :status")
                .expressionAttributeNames(Map.of("#s", "status"))
                .expressionAttributeValues(Map.of(":status", AttributeValue.builder().s(status.name()).build()))
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        return Mono.fromCompletionStage(client.updateItem(request))
                .map(UpdateItemResponse::attributes)
                .map(this::mapToOrder);
    }

    @Override
    public Flux<Order> findExpiredReservations(LocalDateTime now) {
        ScanRequest request = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .filterExpression("#s = :status AND expiresAt < :now")
                .expressionAttributeNames(Map.of("#s", "status"))
                .expressionAttributeValues(Map.of(
                        ":status", AttributeValue.builder().s(TicketStatus.RESERVED.name()).build(),
                        ":now", AttributeValue.builder().s(now.toString()).build()))
                .build();

        return Mono.fromCompletionStage(client.scan(request))
                .flatMapMany(response -> Flux.fromIterable(response.items()))
                .map(this::mapToOrder);
    }

    private Order mapToOrder(Map<String, AttributeValue> item) {
        return new Order(
                item.get("id").s(),
                item.get("eventId").s(),
                item.get("userId").s(),
                Integer.parseInt(item.get("quantity").n()),
                TicketStatus.valueOf(item.get("status").s()),
                LocalDateTime.parse(item.get("createdAt").s()),
                LocalDateTime.parse(item.get("expiresAt").s()));
    }
}
