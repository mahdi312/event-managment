package com.event.management.eventservice.controller;

import com.event.management.eventservice.dto.EventRequest;
import com.event.management.eventservice.dto.EventResponse;
import com.event.management.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management APIs")
@Slf4j
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create a new event", description = "Creates a new event. Requires ADMIN or EVENT_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EVENT_MANAGER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("POST /api/v1/events createEvent title={}", request.getTitle());
        EventResponse response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all events", description = "Retrieves a list of all events.")
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        log.debug("GET /api/v1/events getAllEvents");
        List<EventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get event by ID", description = "Retrieves event by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        log.debug("GET /api/v1/events/{}", id);
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Update an existing event", description = "Updates an existing event. Requires ADMIN or EVENT_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVENT_MANAGER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        log.info("PUT /api/v1/events/{}", id);
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete an event", description = "Deletes an event by its ID. Requires ADMIN or EVENT_MANAGER role.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVENT_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        log.warn("DELETE /api/v1/events/{}", id);
        eventService.deleteEvent(id);
    }

    @Operation(summary = "Decrement available tickets for an event", description = "Decrements the available tickets for an event.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/decrement-tickets")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVENT_MANAGER')")
    public ResponseEntity<EventResponse> decrementEventTickets(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        log.info("PUT /api/v1/events/{}/decrement-tickets", id);
        Integer numberOfTickets = request.get("numberOfTickets");
        if (numberOfTickets == null || numberOfTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets to decrement must be positive.");
        }
        EventResponse response = eventService.decrementAvailableTickets(id, numberOfTickets);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Increment available tickets for an event", description = "Increments the available tickets for an event.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/increment-tickets")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVENT_MANAGER')")
    public ResponseEntity<EventResponse> incrementEventTickets(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer numberOfTickets = request.get("numberOfTickets");
        if (numberOfTickets == null || numberOfTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets to increment must be positive.");
        }
        EventResponse response = eventService.incrementAvailableTickets(id, numberOfTickets);
        return ResponseEntity.ok(response);
    }
}