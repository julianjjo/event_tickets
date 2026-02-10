package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Mono<Event> execute(Event event) {
        String id = event.id() == null ? UUID.randomUUID().toString() : event.id();
        return eventRepository.save(event.withId(id));
    }
}
