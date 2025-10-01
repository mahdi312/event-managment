package com.event.management.eventservice.mapper;

import com.event.management.eventservice.dto.EventRequest;
import com.event.management.eventservice.dto.EventResponse;
import com.event.management.eventservice.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEntity(EventRequest request);

    List<Event> toEntityList(List<EventRequest> requests);

    EventResponse toDto(Event event);

    List<EventResponse> toDtoList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "availableTickets", ignore = true)
    void updateEventFromDto(EventRequest request, @MappingTarget Event event);



}