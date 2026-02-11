package com.nequi.ticket.booking.infrastructure.entrypoint.rest;

import com.nequi.ticket.booking.application.usecase.ConfirmPurchaseUseCase;
import com.nequi.ticket.booking.application.usecase.GetOrderUseCase;
import com.nequi.ticket.booking.application.usecase.ReserveSeatUseCase;
import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.ReservationRequest;
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
class PurchaseControllerTest {

        @Mock
        private ReserveSeatUseCase reserveSeatUseCase;

        @Mock
        private ConfirmPurchaseUseCase confirmPurchaseUseCase;

        @Mock
        private GetOrderUseCase getOrderUseCase;

        @InjectMocks
        private PurchaseController purchaseController;

        @Test
        void shouldReserveSeat() {
                ReservationRequest request = new ReservationRequest("event-1", "user-1", 2);
                Order order = new Order("order-1", "event-1", "user-1", 2, TicketStatus.RESERVED,
                                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

                when(reserveSeatUseCase.execute("event-1", "user-1", 2)).thenReturn(Mono.just(order));

                StepVerifier.create(purchaseController.reserve(request))
                                .expectNext(order)
                                .verifyComplete();
        }

        @Test
        void shouldConfirmPurchase() {
                String orderId = "order-1";
                when(confirmPurchaseUseCase.execute(orderId)).thenReturn(Mono.just(orderId));

                StepVerifier.create(purchaseController.confirm(orderId))
                                .expectNext(orderId)
                                .verifyComplete();
        }

        @Test
        void shouldGetStatus() {
                String orderId = "order-1";
                Order order = new Order(orderId, "event-1", "user-1", 2, TicketStatus.PENDING_CONFIRMATION,
                                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
                when(getOrderUseCase.execute(orderId)).thenReturn(Mono.just(order));

                StepVerifier.create(purchaseController.getStatus(orderId))
                                .expectNext(order)
                                .verifyComplete();
        }
}
