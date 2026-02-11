package com.nequi.ticket.booking.infrastructure.entrypoint.rest.advice;

import com.nequi.ticket.booking.domain.exception.*;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleEventNotFound() {
        EventNotFoundException ex = new EventNotFoundException("1");
        ResponseEntity<ErrorResponse> response = handler.handleEventNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("EVENT_NOT_FOUND", response.getBody().code());
    }

    @Test
    void shouldHandleOrderNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException("1");
        ResponseEntity<ErrorResponse> response = handler.handleOrderNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ORDER_NOT_FOUND", response.getBody().code());
    }

    @Test
    void shouldHandleInsufficientTickets() {
        InsufficientTicketsException ex = new InsufficientTicketsException("1", 10);
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientTickets(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INSUFFICIENT_TICKETS", response.getBody().code());
    }

    @Test
    void shouldHandleInvalidReservation() {
        InvalidReservationException ex = new InvalidReservationException("Invalid");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidReservation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_RESERVATION", response.getBody().code());
    }

    @Test
    void shouldHandleInvalidOrderState() {
        InvalidOrderStateException ex = new InvalidOrderStateException("1", "CONFIRMED");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidOrderState(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("INVALID_ORDER_STATE", response.getBody().code());
    }

    @Test
    void shouldHandleReservationExpired() {
        ReservationExpiredException ex = new ReservationExpiredException("1");
        ResponseEntity<ErrorResponse> response = handler.handleReservationExpired(ex);

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertEquals("RESERVATION_EXPIRED", response.getBody().code());
    }

    @Test
    void shouldHandleDomainException() {
        DomainException ex = new DomainException("Domain error") {
        };
        ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("DOMAIN_ERROR", response.getBody().code());
    }

    @Test
    void shouldHandleGeneralException() {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}
