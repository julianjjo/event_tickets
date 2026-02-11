package com.nequi.ticket.booking.infrastructure.entrypoint.rest;

import com.nequi.ticket.booking.application.usecase.CreateEventUseCase;
import com.nequi.ticket.booking.application.usecase.GetEventUseCase;
import com.nequi.ticket.booking.application.usecase.ListEventsUseCase;
import com.nequi.ticket.booking.domain.exception.EventNotFoundException;
import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.EventRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final ListEventsUseCase listEventsUseCase;
    private final GetEventUseCase getEventUseCase;

    public EventController(CreateEventUseCase createEventUseCase,
            ListEventsUseCase listEventsUseCase, GetEventUseCase getEventUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.listEventsUseCase = listEventsUseCase;
        this.getEventUseCase = getEventUseCase;
    }

    @GetMapping
    public Flux<Event> listEvents() {
        return listEventsUseCase.execute();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Event> createEvent(@Valid @RequestBody EventRequest request) {
        Event newEvent = new Event(
                null,
                request.name(),
                request.date(),
                request.location(),
                request.totalTickets(),
                request.totalTickets(),
                0L);
        return createEventUseCase.execute(newEvent);
    }

    @GetMapping("/{id}/availability")
    public Mono<Integer> getAvailability(@PathVariable String id) {
        return getEventUseCase.execute(id)
                .switchIfEmpty(Mono.error(new EventNotFoundException(id)))
                .map(Event::availableTickets);
    }
}
