package com.event.management.ticketingservice.service;

import com.event.management.ticketingservice.dto.EventDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "event-service", url = "${event-service.url:http://event-service:8082}")
public interface EventServiceClient {

    @GetMapping("/api/v1/events/{id}")
    EventDetailsDto getEventById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Long id);

    @PutMapping("/api/v1/events/{id}/decrement-tickets")
    EventDetailsDto decrementEventTickets(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Long id, @RequestBody Map<String, Integer> request);

    @PutMapping("/api/v1/events/{id}/increment-tickets")
    EventDetailsDto incrementEventTickets(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Long id, @RequestBody Map<String, Integer> request);
}