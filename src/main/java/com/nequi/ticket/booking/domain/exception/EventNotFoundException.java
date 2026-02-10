package com.nequi.ticket.booking.domain.exception;

public class EventNotFoundException extends DomainException {
    public EventNotFoundException(String id) {
        super(String.format("Event with ID %s not found", id));
    }
}
