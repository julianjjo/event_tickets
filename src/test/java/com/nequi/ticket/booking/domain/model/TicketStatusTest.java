package com.nequi.ticket.booking.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusTest {

    @Test
    void shouldHaveAllExpectedStatuses() {
        TicketStatus[] values = TicketStatus.values();
        assertTrue(values.length > 0);
    }

    @Test
    void shouldConvertFromString() {
        assertEquals(TicketStatus.RESERVED, TicketStatus.valueOf("RESERVED"));
        assertEquals(TicketStatus.SOLD, TicketStatus.valueOf("SOLD"));
        assertEquals(TicketStatus.EXPIRED, TicketStatus.valueOf("EXPIRED"));
        assertEquals(TicketStatus.FAILED, TicketStatus.valueOf("FAILED"));
        assertEquals(TicketStatus.PENDING_CONFIRMATION, TicketStatus.valueOf("PENDING_CONFIRMATION"));
    }
}
