package com.nequi.ticket.booking.infrastructure.entrypoint.rest.advice;

import com.nequi.ticket.booking.domain.exception.*;
import com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InsufficientTicketsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientTickets(InsufficientTicketsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INSUFFICIENT_TICKETS", ex.getMessage());
    }

    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReservation(InvalidReservationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION", ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderState(InvalidOrderStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "INVALID_ORDER_STATE", ex.getMessage());
    }

    @ExceptionHandler(ReservationExpiredException.class)
    public ResponseEntity<ErrorResponse> handleReservationExpired(ReservationExpiredException ex) {
        return buildResponse(HttpStatus.GONE, "RESERVATION_EXPIRED", ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "DOMAIN_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }
}
