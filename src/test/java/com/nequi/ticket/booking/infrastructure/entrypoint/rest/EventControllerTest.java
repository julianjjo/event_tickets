package com.nequi.ticket.booking.infrastructure.entrypoint.rest;

import com.nequi.ticket.booking.application.usecase.CreateEventUseCase;
import com.nequi.ticket.booking.application.usecase.GetEventUseCase;
import com.nequi.ticket.booking.application.usecase.ListEventsUseCase;
import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private CreateEventUseCase createEventUseCase;

    @Mock
    private ListEventsUseCase listEventsUseCase;

    @Mock
    private GetEventUseCase getEventUseCase;

    @InjectMocks
    private EventController eventController;

    @Test
    void shouldListEvents() {
        Event event = new Event("1", "Event 1", LocalDateTime.now(), "L1", 100, 100, 0L);
        when(listEventsUseCase.execute()).thenReturn(Flux.just(event));

        StepVerifier.create(eventController.listEvents())
                .expectNext(event)
                .verifyComplete();
    }

    @Test
    void shouldCreateEvent() {
        EventRequest request = new EventRequest("New Event", LocalDateTime.now(), "Location", 100);
        Event savedEvent = new Event("1", request.name(), request.date(), request.location(), 100, 100, 0L);

        when(createEventUseCase.execute(any(Event.class))).thenReturn(Mono.just(savedEvent));

        StepVerifier.create(eventController.createEvent(request))
                .expectNext(savedEvent)
                .verifyComplete();
    }

    @Test
    void shouldGetAvailability() {
        String eventId = "1";
        Event event = new Event(eventId, "Event 1", LocalDateTime.now(), "L1", 100, 45, 0L);
        when(getEventUseCase.execute(eventId)).thenReturn(Mono.just(event));

        StepVerifier.create(eventController.getAvailability(eventId))
                .expectNext(45)
                .verifyComplete();
    }
}
