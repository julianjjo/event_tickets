package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRequestTest {

    @Test
    void shouldCreateReservationRequest() {
        ReservationRequest request = new ReservationRequest("event-1", "user-1", 3);

        assertEquals("event-1", request.eventId());
        assertEquals("user-1", request.userId());
        assertEquals(3, request.quantity());
    }
}
