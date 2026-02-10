package com.nequi.ticket.booking.domain.model;

import java.time.LocalDateTime;

public record Order(
        String id,
        String eventId,
        String userId,
        Integer quantity,
        TicketStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt) {
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
