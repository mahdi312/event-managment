package com.event.management.ticketingservice.service;

import com.event.management.ticketingservice.event.TicketBookedEvent;
import com.event.management.ticketingservice.event.TicketCancelledEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // To serialize events to JSON

    public void sendTicketBookedEvent(TicketBookedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ticket-events", "ticket-booked", message); // Topic, Key, Message
            log.info("Sent TicketBookedEvent: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing TicketBookedEvent to JSON: {}", e.getMessage());
        }
    }

    public void sendTicketCancelledEvent(TicketCancelledEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ticket-events", "ticket-cancelled", message); // Topic, Key, Message
            log.info("Sent TicketCancelledEvent: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing TicketCancelledEvent to JSON: {}", e.getMessage());
        }
    }
}