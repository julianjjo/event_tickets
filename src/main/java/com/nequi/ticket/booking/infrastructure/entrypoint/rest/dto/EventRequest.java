package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EventRequest(
                @NotBlank(message = "Event name is required") String name,

                @NotNull(message = "Event date is required") @Future(message = "Event date must be in the future") LocalDateTime date,

                @NotBlank(message = "Event location is required") String location,

                @NotNull(message = "Total tickets is required") @Positive(message = "Total tickets must be positive") Integer totalTickets) {
}
