package com.nequi.ticket.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingSystemApplicationTest {

    @Test
    void shouldHaveMainMethod() {
        // Verify the main class exists and the method is accessible
        assertDoesNotThrow(() -> BookingSystemApplication.class.getDeclaredMethod("main", String[].class));
    }
}
