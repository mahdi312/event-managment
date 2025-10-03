package com.event.management.eventservice.service;

import com.event.management.eventservice.dto.EventRequest;
import com.event.management.eventservice.dto.EventResponse;
import com.event.management.eventservice.entity.Event;
import com.event.management.eventservice.exception.EventNotFoundException;
import com.event.management.eventservice.mapper.EventMapper;
import com.event.management.eventservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEventById_shouldReturnMappedDto_whenFound() {
        Event event = new Event();
        event.setId(1L);
        EventResponse response = new EventResponse();
        response.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(response);

        EventResponse result = eventService.getEventById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEventById_shouldThrow_whenNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.getEventById(999L))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void createEvent_shouldPersist_andReturnDto() {
        EventRequest req = new EventRequest();
        req.setTitle("Test");
        req.setTotalTickets(10);
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));

        Event entity = new Event();
        entity.setId(5L);
        when(eventMapper.toEntity(any())).thenReturn(entity);
        when(eventRepository.save(any())).thenReturn(entity);
        EventResponse dto = new EventResponse();
        dto.setId(5L);
        when(eventMapper.toDto(any())).thenReturn(dto);

        EventResponse result = eventService.createEvent(req);
        assertThat(result.getId()).isEqualTo(5L);
    }
}


