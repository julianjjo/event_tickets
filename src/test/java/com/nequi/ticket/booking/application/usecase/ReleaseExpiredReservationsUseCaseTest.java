package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

class ReleaseExpiredReservationsUseCaseTest {

    private ReleaseExpiredReservationsUseCase releaseExpiredReservationsUseCase;
    private ManualOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new ManualOrderRepository();
        releaseExpiredReservationsUseCase = new ReleaseExpiredReservationsUseCase(orderRepository);
    }

    @Test
    void shouldReleaseExpiredReservations() {
        Order expiredOrder = new Order("order-1", "event-1", "user-1", 1, TicketStatus.RESERVED,
                LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(10));
        orderRepository.setExpiredOrders(expiredOrder);

        StepVerifier.create(releaseExpiredReservationsUseCase.execute())
                .verifyComplete();
    }

    private static class ManualOrderRepository implements OrderRepository {
        private Order expiredOrder;

        public void setExpiredOrders(Order order) {
            this.expiredOrder = order;
        }

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
            return expiredOrder != null ? Flux.just(expiredOrder) : Flux.empty();
        }

        @Override
        public Mono<Order> updateStatus(String id, TicketStatus status) {
            return Mono.just(expiredOrder);
        }
    }
}
