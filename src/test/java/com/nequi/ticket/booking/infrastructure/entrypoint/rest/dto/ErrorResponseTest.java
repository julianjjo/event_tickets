package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithCodeAndMessage() {
        ErrorResponse response = new ErrorResponse("ERROR_CODE", "Something went wrong");

        assertEquals("ERROR_CODE", response.code());
        assertEquals("Something went wrong", response.message());
        assertNotNull(response.timestamp());
    }

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 1, 12, 0);
        ErrorResponse response = new ErrorResponse("ERROR_CODE", "Message", timestamp);

        assertEquals("ERROR_CODE", response.code());
        assertEquals("Message", response.message());
        assertEquals(timestamp, response.timestamp());
    }
}
