package com.nequi.ticket.booking.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
                @NotBlank(message = "Event ID is required") String eventId,

                @NotBlank(message = "User ID is required") String userId,

                @NotNull(message = "Quantity is required") @Min(value = 1, message = "Minimum 1 ticket per order") @Max(value = 10, message = "Maximum 10 tickets per order") Integer quantity) {
}
