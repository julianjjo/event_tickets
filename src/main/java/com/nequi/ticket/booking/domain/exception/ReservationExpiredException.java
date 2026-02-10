package com.nequi.ticket.booking.domain.exception;

public class ReservationExpiredException extends DomainException {
    public ReservationExpiredException(String id) {
        super("Reservation " + id + " has expired");
    }
}
