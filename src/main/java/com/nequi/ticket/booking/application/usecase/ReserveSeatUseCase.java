package com.nequi.ticket.booking.application.usecase;

import com.nequi.ticket.booking.domain.exception.EventNotFoundException;
import com.nequi.ticket.booking.domain.exception.InsufficientTicketsException;
import com.nequi.ticket.booking.domain.exception.InvalidReservationException;
import com.nequi.ticket.booking.domain.model.Order;
import com.nequi.ticket.booking.domain.model.TicketStatus;
import com.nequi.ticket.booking.domain.port.EventRepository;
import com.nequi.ticket.booking.domain.port.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReserveSeatUseCase {

    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    public ReserveSeatUseCase(EventRepository eventRepository, OrderRepository orderRepository) {
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
    }

    public Mono<Order> execute(String eventId, String userId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return Mono.error(new InvalidReservationException("Quantity must be at least 1"));
        }
        if (quantity > 10) {
            return Mono.error(new InvalidReservationException("Maximum 10 tickets per order"));
        }

        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new EventNotFoundException(eventId)))
                .flatMap(event -> eventRepository
                        .updateAvailability(eventId, quantity, event.version())
                        .onErrorResume(e -> Mono.error(
                                new InsufficientTicketsException(eventId, quantity)))
                        .flatMap(updatedEvent -> orderRepository.save(new Order(
                                UUID.randomUUID().toString(),
                                eventId,
                                userId,
                                quantity,
                                TicketStatus.RESERVED,
                                LocalDateTime.now(),
                                LocalDateTime.now().plusMinutes(10)))));
    }
}
