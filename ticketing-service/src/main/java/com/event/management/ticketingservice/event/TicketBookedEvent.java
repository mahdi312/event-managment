package com.event.management.ticketingservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketBookedEvent {
    private Long bookingId;
    private Long eventId;
    private Long userId;
    private Integer numberOfTickets;
    private String eventTitle;
    private LocalDateTime bookingTime;
}