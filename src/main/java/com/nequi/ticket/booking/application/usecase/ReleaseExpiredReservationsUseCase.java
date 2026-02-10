package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ReleaseExpiredReservationsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReleaseExpiredReservationsUseCase.class);
    private final OrderRepository orderRepository;

    public ReleaseExpiredReservationsUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<Void> execute() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking for expired reservations at {}", now);

        return orderRepository.findExpiredReservations(now)
                .flatMap(order -> {
                    log.info("Releasing expired order: {} for event: {}", order.id(), order.eventId());
                    return orderRepository.updateStatus(order.id(), TicketStatus.EXPIRED)
                            .onErrorResume(e -> {
                                log.error("Failed to mark order as expired {}: {}", order.id(), e.getMessage());
                                return Mono.empty();
                            });
                })
                .then();
    }
}
