package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<Order> execute(String id) {
        return orderRepository.findById(id);
    }
}
