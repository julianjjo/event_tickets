package com.nequi.ticket.booking.domain.port;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface OrderRepository {
    Mono<Order> findById(String id);

    Mono<Order> save(Order order);

    Mono<Order> updateStatus(String orderId, TicketStatus status);

    Flux<Order> findExpiredReservations(LocalDateTime now);
}
