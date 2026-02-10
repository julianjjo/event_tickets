package com.nequi.ticket.booking.infrastructure.adapter;

import com.nequi.ticket.booking.application.usecase.ReleaseExpiredReservationsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationScheduler {

    private final ReleaseExpiredReservationsUseCase releaseExpiredReservationsUseCase;

    public ReservationExpirationScheduler(ReleaseExpiredReservationsUseCase releaseExpiredReservationsUseCase) {
        this.releaseExpiredReservationsUseCase = releaseExpiredReservationsUseCase;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredReservations() {
        releaseExpiredReservationsUseCase.execute().subscribe();
    }
}
