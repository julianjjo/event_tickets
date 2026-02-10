package com.nequi.ticket.booking.domain.exception;

public class InsufficientTicketsException extends DomainException {
    public InsufficientTicketsException(String eventId, Integer requested) {
        super(String.format("Not enough tickets available for event %s. Requested: %d", eventId, requested));
    }
}
