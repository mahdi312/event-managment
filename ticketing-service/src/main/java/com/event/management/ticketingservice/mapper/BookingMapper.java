package com.event.management.ticketingservice.mapper;

import com.event.management.ticketingservice.dto.BookingResponse;
import com.event.management.ticketingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "eventTitle", ignore = true)
    @Mapping(target = "username", ignore = true)
    BookingResponse toDto(Booking booking);

    List<BookingResponse> toDtoList(List<Booking> bookings);
}