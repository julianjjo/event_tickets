package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ListEventsUseCase {

    private final EventRepository eventRepository;

    public ListEventsUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Flux<Event> execute() {
        return eventRepository.findAll();
    }
}
