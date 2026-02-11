package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.OrderRepository;
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
class GetOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderUseCase getOrderUseCase;

    @Test
    void shouldReturnOrderWhenFound() {
        String orderId = "order-1";
        Order order = new Order(orderId, "event-1", "user-1", 2, TicketStatus.RESERVED, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10));

        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

        StepVerifier.create(getOrderUseCase.execute(orderId))
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        String orderId = "order-1";

        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(getOrderUseCase.execute(orderId))
                .verifyComplete();
    }
}
