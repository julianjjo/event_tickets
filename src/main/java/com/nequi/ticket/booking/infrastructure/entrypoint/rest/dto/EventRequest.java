package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import java.time.LocalDateTime;

public record EventRequest(
        String name,
        LocalDateTime date,
        String location,
        Integer totalTickets) {
}
