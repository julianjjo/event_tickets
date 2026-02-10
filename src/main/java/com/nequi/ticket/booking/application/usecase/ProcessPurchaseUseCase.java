package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.EventRepository;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProcessPurchaseUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPurchaseUseCase.class);
    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;

    public ProcessPurchaseUseCase(OrderRepository orderRepository, EventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
    }

    public Mono<Void> execute(String orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    log.info("Processing purchase for order: {}", orderId);
                    return eventRepository.findById(order.eventId())
                            .flatMap(event -> eventRepository
                                    .updateAvailability(order.eventId(), order.quantity(), event.version())
                                    .flatMap(updatedEvent -> orderRepository.updateStatus(orderId, TicketStatus.SOLD))
                                    .onErrorResume(e -> {
                                        log.warn("Failed to update availability for order {}: {}", orderId,
                                                e.getMessage());
                                        return orderRepository.updateStatus(orderId, TicketStatus.FAILED);
                                    }))
                            .switchIfEmpty(Mono.error(new RuntimeException("Event not found for order: " + orderId)));
                })
                .doOnError(e -> log.error("Error processing purchase for order {}: {}", orderId, e.getMessage()))
                .then();
    }
}
