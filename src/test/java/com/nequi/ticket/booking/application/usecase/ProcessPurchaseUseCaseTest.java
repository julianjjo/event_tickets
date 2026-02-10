package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Event;
import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

class ProcessPurchaseUseCaseTest {

    private ProcessPurchaseUseCase processPurchaseUseCase;
    private ManualOrderRepository orderRepository;
    private ManualEventRepository eventRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new ManualOrderRepository();
        eventRepository = new ManualEventRepository();
        processPurchaseUseCase = new ProcessPurchaseUseCase(orderRepository, eventRepository);
    }

    @Test
    void shouldProcessPurchaseSuccessfully() {
        Order order = new Order("order-1", "event-1", "user-1", 2, TicketStatus.PENDING_CONFIRMATION,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        orderRepository.setNextOrder(order);
        eventRepository.setNextEvent(new Event("event-1", "Test", LocalDateTime.now(), "Loc", 100, 100, 0L));

        StepVerifier.create(processPurchaseUseCase.execute("order-1"))
                .verifyComplete();
    }

    private static class ManualEventRepository implements com.nequi.ticket.booking.domain.port.EventRepository {
        private Event nextEvent;

        public void setNextEvent(Event event) {
            this.nextEvent = event;
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
            return Mono.just(nextEvent);
        }
    }

    private static class ManualOrderRepository implements OrderRepository {
        private Order nextOrder;

        public void setNextOrder(Order order) {
            this.nextOrder = order;
        }

        @Override
        public Mono<Order> findById(String id) {
            return Mono.justOrEmpty(nextOrder);
        }

        @Override
        public Mono<Order> save(Order order) {
            return Mono.just(order);
        }

        @Override
        public Flux<Order> findExpiredReservations(LocalDateTime now) {
            return Flux.empty();
        }

        @Override
        public Mono<Order> updateStatus(String id, TicketStatus status) {
            return Mono.just(nextOrder);
        }
    }
}
