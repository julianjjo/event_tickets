package com.nequi.ticket.booking.infrastructure.adapter;

import com.nequi.ticket.booking.application.usecase.ReleaseExpiredReservationsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationExpirationSchedulerTest {

    @Mock
    private ReleaseExpiredReservationsUseCase releaseExpiredReservationsUseCase;

    @InjectMocks
    private ReservationExpirationScheduler scheduler;

    @Test
    void shouldCallReleaseExpiredReservations() {
        when(releaseExpiredReservationsUseCase.execute()).thenReturn(Mono.empty());

        scheduler.cleanupExpiredReservations();

        verify(releaseExpiredReservationsUseCase).execute();
    }
}
