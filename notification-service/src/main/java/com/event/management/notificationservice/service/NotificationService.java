package com.event.management.notificationservice.service;

import com.event.management.notificationservice.dto.UserDetailsDto;
import com.event.management.notificationservice.event.TicketCancelledEvent;
import com.event.management.notificationservice.event.TicketBookedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
     private final String SERVICE_AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzE1MzE3OTM1LCJleHAiOjE3MTUzNjExMzV9.q7T-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S-N2n-S2S";

    @KafkaListener(topics = "ticket-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTicketEvents(String message) {
        log.info("Received Kafka message: {}", message);
        try {
            if (message.contains("ticketBookedEvent")) {
                TicketBookedEvent event = objectMapper.readValue(message, TicketBookedEvent.class);
                handleTicketBookedEvent(event);
            } else if (message.contains("ticketCancelledEvent")) {
                TicketCancelledEvent event = objectMapper.readValue(message, TicketCancelledEvent.class);
                handleTicketCancelledEvent(event);
            } else {
                log.warn("Unknown event type received: {}", message);
            }
        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", e.getMessage());
        }
    }

    private void handleTicketBookedEvent(TicketBookedEvent event) {
        log.info("Processing Ticket Booked Event for Booking ID: {}", event.getBookingId());
        try {
            UserDetailsDto user = userServiceClient.getUserById(SERVICE_AUTH_TOKEN, event.getUserId());
            if (user != null) {
                String subject = "Your Ticket Booking Confirmation for " + event.getEventTitle();
                String body = String.format(
                        "Dear %s,\n\n" +
                                "Your booking for %d tickets to '%s' on %s has been confirmed.\n" +
                                "Booking ID: %d\n\n" +
                                "Thank you for using our service!\n\n" +
                                "Best regards,\nEvent Management Team",
                        user.getUsername(),
                        event.getNumberOfTickets(),
                        event.getEventTitle(),
                        event.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        event.getBookingId()
                );
                emailService.sendEmail(user.getEmail(), subject, body);
            } else {
                log.error("User not found for ID: {} for booking {}", event.getUserId(), event.getBookingId());
            }
        } catch (Exception e) {
            log.error("Error handling TicketBookedEvent for booking {}: {}", event.getBookingId(), e.getMessage());

        }
    }

    private void handleTicketCancelledEvent(TicketCancelledEvent event) {
        log.info("Processing Ticket Cancelled Event for Booking ID: {}", event.getBookingId());
        try {
            UserDetailsDto user = userServiceClient.getUserById(SERVICE_AUTH_TOKEN, event.getUserId());
            if (user != null) {
                String subject = "Your Ticket Booking Cancellation Confirmation";
                String body = String.format(
                        "Dear %s,\n\n" +
                                "Your booking (ID: %d) for %d tickets has been successfully cancelled.\n" +
                                "Cancellation Time: %s\n\n" +
                                "A refund will be processed shortly.\n\n" +
                                "Best regards,\nEvent Management Team",
                        user.getUsername(),
                        event.getBookingId(),
                        event.getNumberOfTickets(),
                        event.getCancellationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
                emailService.sendEmail(user.getEmail(), subject, body);
            } else {
                log.error("User not found for ID: {} for booking {}", event.getUserId(), event.getBookingId());
            }
        } catch (Exception e) {
            log.error("Error handling TicketCancelledEvent for booking {}: {}", event.getBookingId(), e.getMessage());

        }
    }
}