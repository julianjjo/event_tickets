package com.nequi.ticket.booking.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void shouldCreateEventNotFoundException() {
        EventNotFoundException ex = new EventNotFoundException("evt-1");
        assertTrue(ex.getMessage().contains("evt-1"));
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void shouldCreateOrderNotFoundException() {
        OrderNotFoundException ex = new OrderNotFoundException("ord-1");
        assertTrue(ex.getMessage().contains("ord-1"));
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void shouldCreateInsufficientTicketsException() {
        InsufficientTicketsException ex = new InsufficientTicketsException("evt-1", 5);
        assertTrue(ex.getMessage().contains("evt-1"));
        assertTrue(ex.getMessage().contains("5"));
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void shouldCreateInvalidReservationException() {
        InvalidReservationException ex = new InvalidReservationException("Invalid");
        assertEquals("Invalid", ex.getMessage());
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void shouldCreateInvalidOrderStateException() {
        InvalidOrderStateException ex = new InvalidOrderStateException("ord-1", "CONFIRMED");
        assertTrue(ex.getMessage().contains("ord-1"));
        assertTrue(ex.getMessage().contains("CONFIRMED"));
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void shouldCreateReservationExpiredException() {
        ReservationExpiredException ex = new ReservationExpiredException("ord-1");
        assertTrue(ex.getMessage().contains("ord-1"));
        assertInstanceOf(DomainException.class, ex);
    }
}
