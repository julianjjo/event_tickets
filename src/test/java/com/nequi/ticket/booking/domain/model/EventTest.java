package com.nequi.ticket.booking.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void shouldCreateEvent() {
        LocalDateTime date = LocalDateTime.now();
        Event event = new Event("1", "Concert", date, "Stadium", 100, 100, 0L);

        assertEquals("1", event.id());
        assertEquals("Concert", event.name());
        assertEquals(date, event.date());
        assertEquals("Stadium", event.location());
        assertEquals(100, event.totalCapacity());
        assertEquals(100, event.availableTickets());
        assertEquals(0L, event.version());
    }

    @Test
    void shouldUpdateAvailableTickets() {
        Event event = new Event("1", "Concert", LocalDateTime.now(), "Stadium", 100, 100, 0L);
        Event updated = new Event(event.id(), event.name(), event.date(), event.location(), event.totalCapacity(), 80,
                1L);

        assertEquals(80, updated.availableTickets());
        assertEquals(1L, updated.version());
    }

    @Test
    void shouldCreateEventWithId() {
        Event event = new Event(null, "Concert", LocalDateTime.now(), "Stadium", 100, 100, 0L);
        Event withId = event.withId("new-id");

        assertEquals("new-id", withId.id());
        assertEquals("Concert", withId.name());
        assertEquals(100, withId.totalCapacity());
    }

    @Test
    void shouldCreateEventWithAvailableTickets() {
        Event event = new Event("1", "Concert", LocalDateTime.now(), "Stadium", 100, 100, 0L);
        Event updated = event.withAvailableTickets(50);

        assertEquals("1", updated.id());
        assertEquals(50, updated.availableTickets());
        assertEquals(100, updated.totalCapacity());
    }
}
