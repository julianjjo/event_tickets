package com.nequi.ticket.booking.domain.port;

import com.nequi.ticket.booking.domain.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository {
    Mono<Event> findById(String id);

    Flux<Event> findAll();

    Mono<Event> save(Event event);

    Mono<Event> updateAvailability(String eventId, Integer decrement, Long currentVersion);
}
