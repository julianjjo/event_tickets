package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private GetEventUseCase getEventUseCase;

    @Test
    void shouldReturnEventWhenFound() {
        String eventId = "event-1";
        Event event = new Event(eventId, "Test Event", LocalDateTime.now(), "Location", 100, 100, 0L);

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));

        StepVerifier.create(getEventUseCase.execute(eventId))
                .expectNext(event)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        String eventId = "event-1";

        when(eventRepository.findById(eventId)).thenReturn(Mono.empty());

        StepVerifier.create(getEventUseCase.execute(eventId))
                .verifyComplete();
    }
}
