package com.event.management.ticketingservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    @NotNull(message = "Number of tickets cannot be null")
    @Min(value = 1, message = "Number of tickets must be at least 1")
    private Integer numberOfTickets;
}