package com.nequi.ticket.booking.domain.model;

import java.time.LocalDateTime;

public record Event(
        String id,
        String name,
        LocalDateTime date,
        String location,
        Integer totalCapacity,
        Integer availableTickets,
        Long version) {
    public Event withId(String id) {
        return new Event(id, name, date, location, totalCapacity, availableTickets, version);
    }

    public Event withAvailableTickets(Integer count) {
        return new Event(id, name, date, location, totalCapacity, count, version);
    }
}
