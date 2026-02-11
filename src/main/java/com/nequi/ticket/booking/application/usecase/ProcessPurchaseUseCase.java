package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.exception.OrderNotFoundException;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProcessPurchaseUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPurchaseUseCase.class);
    private final OrderRepository orderRepository;

    public ProcessPurchaseUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<Void> execute(String orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderId)))
                .flatMap(order -> {
                    log.info("Processing purchase for order: {}", orderId);
                    return orderRepository.updateStatus(orderId, TicketStatus.SOLD)
                            .onErrorResume(e -> {
                                log.warn("Failed to confirm order {}: {}", orderId, e.getMessage());
                                return orderRepository.updateStatus(orderId, TicketStatus.FAILED);
                            });
                })
                .doOnError(e -> log.error("Error processing purchase for order {}: {}", orderId, e.getMessage()))
                .then();
    }
}
