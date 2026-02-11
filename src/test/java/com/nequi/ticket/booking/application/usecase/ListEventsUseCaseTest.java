package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListEventsUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ListEventsUseCase listEventsUseCase;

    @Test
    void shouldReturnAllEvents() {
        Event event1 = new Event("1", "Event 1", LocalDateTime.now(), "L1", 100, 100, 0L);
        Event event2 = new Event("2", "Event 2", LocalDateTime.now(), "L2", 50, 50, 0L);

        when(eventRepository.findAll()).thenReturn(Flux.just(event1, event2));

        StepVerifier.create(listEventsUseCase.execute())
                .expectNext(event1)
                .expectNext(event2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoEvents() {
        when(eventRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(listEventsUseCase.execute())
                .verifyComplete();
    }
}
