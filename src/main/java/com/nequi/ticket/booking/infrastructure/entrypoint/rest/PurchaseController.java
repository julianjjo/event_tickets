package com.nequi.ticket.booking.infrastructure.entrypoint.rest;

import com.nequi.ticket.booking.application.usecase.ConfirmPurchaseUseCase;
import com.nequi.ticket.booking.application.usecase.GetOrderUseCase;
import com.nequi.ticket.booking.application.usecase.ReserveSeatUseCase;
import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.ReservationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final ConfirmPurchaseUseCase confirmPurchaseUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public PurchaseController(ReserveSeatUseCase reserveSeatUseCase, ConfirmPurchaseUseCase confirmPurchaseUseCase,
            GetOrderUseCase getOrderUseCase) {
        this.reserveSeatUseCase = reserveSeatUseCase;
        this.confirmPurchaseUseCase = confirmPurchaseUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> reserve(@Valid @RequestBody ReservationRequest request) {
        return reserveSeatUseCase.execute(request.eventId(), request.userId(), request.quantity());
    }

    @PostMapping("/confirm/{orderId}")
    public Mono<String> confirm(@PathVariable String orderId) {
        return confirmPurchaseUseCase.execute(orderId);
    }

    @GetMapping("/{orderId}")
    public Mono<Order> getStatus(@PathVariable String orderId) {
        return getOrderUseCase.execute(orderId);
    }
}
