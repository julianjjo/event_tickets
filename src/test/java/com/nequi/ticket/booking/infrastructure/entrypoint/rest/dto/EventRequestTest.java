package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    @Test
    void shouldCreateEventRequest() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 15, 20, 0);
        EventRequest request = new EventRequest("Concert", date, "Stadium", 500);

        assertEquals("Concert", request.name());
        assertEquals(date, request.date());
        assertEquals("Stadium", request.location());
        assertEquals(500, request.totalTickets());
    }
}
