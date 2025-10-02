package com.event.management.eventservice.service;

import com.event.management.eventservice.dto.EventRequest;
import com.event.management.eventservice.dto.EventResponse;
import com.event.management.eventservice.entity.Event;
import com.event.management.eventservice.exception.EventNotFoundException;
import com.event.management.eventservice.mapper.EventMapper;
import com.event.management.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = eventMapper.toEntity(request);
        event.setCreatedByUserId(getUserIdFromSecurityContext());

        //todo check ticketing service for availablity of tickets
        event.setAvailableTickets(request.getTotalTickets());

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private Long getUserIdFromSecurityContext() {
        //todo --> get from api gateway ---> after implemnting API-GATEWAY microservice
        return 1L;
    }


    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.toDtoList(events);
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        Long currentUserId = getUserIdFromSecurityContext();
        if (!existingEvent.getCreatedByUserId().equals(currentUserId) && !hasRole("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to update this event.");
        }

        int oldTotalTickets = existingEvent.getTotalTickets();
        int oldAvailableTickets = existingEvent.getAvailableTickets();

        eventMapper.updateEventFromDto(request, existingEvent);

        if (!request.getTotalTickets().equals(oldTotalTickets)) {
            int newAvailable = oldAvailableTickets + (request.getTotalTickets() - oldTotalTickets);
            existingEvent.setAvailableTickets(Math.max(0, newAvailable));
        }

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Event updatedEvent = eventRepository.save(existingEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        Long currentUserId = getUserIdFromSecurityContext();
        if (!existingEvent.getCreatedByUserId().equals(currentUserId) && !hasRole("ROLE_ADMIN")) {
            throw new AccessDeniedException("You are not authorized to delete this event.");
        }
        //todo: checking no active bookings exist.
        eventRepository.delete(existingEvent);
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }


    @Transactional
    public EventResponse decrementAvailableTickets(Long eventId, int numberOfTickets) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        if (event.getAvailableTickets() < numberOfTickets) {
            throw new IllegalArgumentException("Not enough tickets available for event " + eventId);
        }

        event.setAvailableTickets(event.getAvailableTickets() - numberOfTickets);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    @Transactional
    public EventResponse incrementAvailableTickets(Long eventId, int numberOfTickets) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        int newAvailableTickets = event.getAvailableTickets() + numberOfTickets;
        if (newAvailableTickets > event.getTotalTickets()) {
            // This scenario might indicate an issue or a refund of more tickets than originally booked.
            // For simplicity, we cap it at totalTickets, or throw an error based on business logic.
            // For now, let's cap it.
            event.setAvailableTickets(event.getTotalTickets());
            // Or throw new IllegalArgumentException("Cannot increment tickets beyond total capacity.");
        } else {
            event.setAvailableTickets(newAvailableTickets);
        }
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }
}