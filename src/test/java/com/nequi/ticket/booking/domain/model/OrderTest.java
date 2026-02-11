package com.nequi.ticket.booking.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(10);
        Order order = new Order("1", "event-1", "user-1", 2, TicketStatus.RESERVED, now, expiry);

        assertEquals("1", order.id());
        assertEquals("event-1", order.eventId());
        assertEquals("user-1", order.userId());
        assertEquals(2, order.quantity());
        assertEquals(TicketStatus.RESERVED, order.status());
        assertEquals(now, order.createdAt());
        assertEquals(expiry, order.expiresAt());
    }

    @Test
    void shouldCheckExpiration() {
        LocalDateTime now = LocalDateTime.now();
        Order expiredOrder = new Order("1", "event-1", "user-1", 2, TicketStatus.RESERVED, now.minusMinutes(15),
                now.minusMinutes(5));
        Order activeOrder = new Order("2", "event-1", "user-1", 2, TicketStatus.RESERVED, now.minusMinutes(5),
                now.plusMinutes(5));

        assertTrue(expiredOrder.isExpired());
        assertFalse(activeOrder.isExpired());
    }
}
