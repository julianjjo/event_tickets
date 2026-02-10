package com.nequi.ticket.booking.domain.exception;

public class InvalidOrderStateException extends DomainException {
    public InvalidOrderStateException(String id, String state) {
        super("Order " + id + " is not in " + state + " state");
    }
}
