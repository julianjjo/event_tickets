package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

public record ReservationRequest(
        String eventId,
        String userId,
        Integer quantity) {
}
