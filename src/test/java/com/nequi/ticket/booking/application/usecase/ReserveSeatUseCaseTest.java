package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.exception.InsufficientTicketsException;
import com.nequi.ticket.booking.domain.exception.InvalidReservationException;
import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.EventRepository;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

class ReserveSeatUseCaseTest {

        private ReserveSeatUseCase reserveSeatUseCase;
        private ManualEventRepository eventRepository;
        private ManualOrderRepository orderRepository;

        @BeforeEach
        void setUp() {
                eventRepository = new ManualEventRepository();
                orderRepository = new ManualOrderRepository();
                reserveSeatUseCase = new ReserveSeatUseCase(eventRepository, orderRepository);
        }

        @Test
        void shouldReserveSeatSuccessfully() {
                Event event = new Event("event-1", "Test", LocalDateTime.now().plusDays(1), "Loc", 100, 100, 0L);
                eventRepository.setNextEvent(event);
                eventRepository.setUpdateShouldFail(false);

                StepVerifier.create(reserveSeatUseCase.execute("event-1", "user-1", 5))
                                .expectNextMatches(order -> order.quantity() == 5
                                                && order.status() == TicketStatus.RESERVED)
                                .verifyComplete();
        }

        @Test
        void shouldFailWhenNotEnoughTickets() {
                Event event = new Event("event-1", "Test", LocalDateTime.now().plusDays(1), "Loc", 100, 2, 0L);
                eventRepository.setNextEvent(event);
                eventRepository.setUpdateShouldFail(true);

                StepVerifier.create(reserveSeatUseCase.execute("event-1", "user-1", 5))
                                .expectError(InsufficientTicketsException.class)
                                .verify();
        }

        @Test
        void shouldFailWhenQuantityIsZeroOrNegative() {
                StepVerifier.create(reserveSeatUseCase.execute("event-1", "user-1", 0))
                                .expectError(InvalidReservationException.class)
                                .verify();

                StepVerifier.create(reserveSeatUseCase.execute("event-1", "user-1", -1))
                                .expectError(InvalidReservationException.class)
                                .verify();
        }

        private static class ManualEventRepository implements EventRepository {
                private Event nextEvent;
                private boolean updateShouldFail = false;

                public void setNextEvent(Event event) {
                        this.nextEvent = event;
                }

                public void setUpdateShouldFail(boolean shouldFail) {
                        this.updateShouldFail = shouldFail;
                }

                @Override
                public Mono<Event> findById(String id) {
                        return Mono.justOrEmpty(nextEvent);
                }

                @Override
                public Flux<Event> findAll() {
                        return Flux.empty();
                }

                @Override
                public Mono<Event> save(Event event) {
                        return Mono.just(event);
                }

                @Override
                public Mono<Event> updateAvailability(String id, Integer quantity, Long version) {
                        if (updateShouldFail) {
                                return Mono.error(new RuntimeException("ConditionalCheckFailedException"));
                        }
                        return Mono.just(nextEvent.withAvailableTickets(nextEvent.availableTickets() - quantity));
                }
        }

        private static class ManualOrderRepository implements OrderRepository {
                @Override
                public Mono<Order> save(Order order) {
                        return Mono.just(order);
                }

                @Override
                public Mono<Order> findById(String id) {
                        return Mono.empty();
                }

                @Override
                public Flux<Order> findExpiredReservations(LocalDateTime now) {
                        return Flux.empty();
                }

                @Override
                public Mono<Order> updateStatus(String id, TicketStatus status) {
                        return Mono.empty();
                }
        }
}
