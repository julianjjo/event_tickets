package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.exception.InvalidOrderStateException;
import com.nequi.ticket.booking.domain.exception.OrderNotFoundException;
import com.nequi.ticket.booking.domain.exception.ReservationExpiredException;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.MessagePublisher;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConfirmPurchaseUseCase {

    private final OrderRepository orderRepository;
    private final MessagePublisher messagePublisher;

    public ConfirmPurchaseUseCase(OrderRepository orderRepository, MessagePublisher messagePublisher) {
        this.orderRepository = orderRepository;
        this.messagePublisher = messagePublisher;
    }

    public Mono<String> execute(String orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    if (order.status() != TicketStatus.RESERVED) {
                        return Mono.error(new InvalidOrderStateException(orderId, "RESERVED"));
                    }
                    if (order.isExpired()) {
                        return Mono.error(new ReservationExpiredException(orderId));
                    }

                    return orderRepository.updateStatus(orderId, TicketStatus.PENDING_CONFIRMATION)
                            .flatMap(updatedOrder -> messagePublisher.publishPurchaseRequest(orderId))
                            .thenReturn(orderId);
                });
    }
}
