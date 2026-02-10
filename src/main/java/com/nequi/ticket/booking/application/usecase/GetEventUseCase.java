package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetEventUseCase {

    private final EventRepository eventRepository;

    public GetEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Mono<Event> execute(String id) {
        return eventRepository.findById(id);
    }
}
