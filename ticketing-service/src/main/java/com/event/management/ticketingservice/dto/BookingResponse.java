package com.event.management.ticketingservice.dto;

import com.event.management.ticketingservice.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private Integer numberOfTickets;
    private Double totalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime bookingTime;
    private LocalDateTime cancellationTime;
}