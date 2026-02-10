package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.port.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

class CreateEventUseCaseTest {

    private CreateEventUseCase createEventUseCase;
    private ManualEventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository = new ManualEventRepository();
        createEventUseCase = new CreateEventUseCase(eventRepository);
    }

    @Test
    void shouldCreateEventSuccessfully() {
        Event event = new Event(null, "Test Event", LocalDateTime.now().plusDays(1), "Loc", 100, 100, 0L);

        StepVerifier.create(createEventUseCase.execute(event))
                .expectNextMatches(e -> e.name().equals("Test Event"))
                .verifyComplete();
    }

    private static class ManualEventRepository implements EventRepository {
        @Override
        public Mono<Event> save(Event event) {
            return Mono.just(event);
        }

        @Override
        public Mono<Event> findById(String id) {
            return Mono.empty();
        }

        @Override
        public Flux<Event> findAll() {
            return Flux.empty();
        }

        @Override
        public Mono<Event> updateAvailability(String id, Integer quantity, Long version) {
            return Mono.empty();
        }
    }
}
