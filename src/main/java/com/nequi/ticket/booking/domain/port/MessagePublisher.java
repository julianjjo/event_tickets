package com.nequi.ticket.booking.domain.port;

import reactor.core.publisher.Mono;

public interface MessagePublisher {
    Mono<Void> publishPurchaseRequest(String orderId);
}
