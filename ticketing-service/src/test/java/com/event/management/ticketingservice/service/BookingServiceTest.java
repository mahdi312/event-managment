package com.event.management.ticketingservice.service;

import com.event.management.ticketingservice.dto.*;
import com.event.management.ticketingservice.entity.Booking;
import com.event.management.ticketingservice.entity.PaymentStatus;
import com.event.management.ticketingservice.exception.InsufficientTicketsException;
import com.event.management.ticketingservice.mapper.BookingMapper;
import com.event.management.ticketingservice.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private EventServiceClient eventServiceClient;
    @Mock
    private PaymentService paymentService;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        org.springframework.security.core.Authentication authentication = new UsernamePasswordAuthenticationToken(
                "1",
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void bookTickets_shouldThrow_whenNotEnoughTickets() {
        BookingRequest request = new BookingRequest();
        request.setEventId(1L);
        request.setNumberOfTickets(5);
        EventDetailsDto event = new EventDetailsDto();
        event.setAvailableTickets(2);
        when(eventServiceClient.getEventById(any(), any())).thenReturn(event);

        assertThatThrownBy(() -> bookingService.bookTickets(request, "Bearer token"))
                .isInstanceOf(InsufficientTicketsException.class);
    }
}


