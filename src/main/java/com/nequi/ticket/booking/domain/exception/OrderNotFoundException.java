package com.nequi.ticket.booking.domain.exception;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String id) {
        super(String.format("Order with ID %s not found", id));
    }
}
