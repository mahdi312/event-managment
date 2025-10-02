package com.event.management.ticketingservice.service;

import com.event.management.ticketingservice.dto.*;
import com.event.management.ticketingservice.entity.Booking;
import com.event.management.ticketingservice.entity.PaymentStatus;
import com.event.management.ticketingservice.event.TicketBookedEvent;
import com.event.management.ticketingservice.event.TicketCancelledEvent;
import com.event.management.ticketingservice.exception.BookingNotFoundException;
import com.event.management.ticketingservice.exception.InsufficientTicketsException;
import com.event.management.ticketingservice.exception.PaymentFailedException;
import com.event.management.ticketingservice.mapper.BookingMapper;
import com.event.management.ticketingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventServiceClient eventServiceClient;
    private final PaymentService paymentService;
    private final KafkaProducerService kafkaProducerService;


    private Long getUserIdFromSecurityContext() {
        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return Long.parseLong(principalName);
        } catch (NumberFormatException e) {
            log.warn("Principal name is not a valid User ID: {}. Using dummy ID 1L.", principalName);
            return 1L;
        }
    }


    private String getJwtTokenFromSecurityContext() {
        return "Bearer test-jwt-token";
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponse bookTickets(BookingRequest request, String jwtToken) {
        Long userId = getUserIdFromSecurityContext();


        EventDetailsDto eventDetails = eventServiceClient.getEventById(jwtToken, request.getEventId());
        if (eventDetails == null) {
            throw new BookingNotFoundException("Event not found with ID: " + request.getEventId());
        }

        if (eventDetails.getAvailableTickets() < request.getNumberOfTickets()) {
            throw new InsufficientTicketsException("Not enough tickets available for event: " + eventDetails.getTitle());
        }

        Double totalAmount = eventDetails.getTicketPrice() * request.getNumberOfTickets();

        Booking booking = Booking.builder()
                .eventId(request.getEventId())
                .userId(userId)
                .numberOfTickets(request.getNumberOfTickets())
                .totalAmount(totalAmount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);

        try {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .cardNumber("1234-5678-9012-3456")
                    .expiryDate("12/25")
                    .cvv("123")
                    .amount(totalAmount)
                    .currency("USD")
                    .transactionRef("BOOKING-" + booking.getId())
                    .build();
            PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);

            if ("SUCCESS".equals(paymentResponse.getPaymentStatus().name())) {
                booking.setPaymentStatus(PaymentStatus.PAID);
                bookingRepository.save(booking);

                Map<String, Integer> decrementRequest = new HashMap<>();
                decrementRequest.put("numberOfTickets", request.getNumberOfTickets());
                eventServiceClient.decrementEventTickets(jwtToken, request.getEventId(), decrementRequest);

                kafkaProducerService.sendTicketBookedEvent(TicketBookedEvent.builder()
                        .bookingId(booking.getId())
                        .eventId(booking.getEventId())
                        .userId(booking.getUserId())
                        .numberOfTickets(booking.getNumberOfTickets())
                        .eventTitle(eventDetails.getTitle())
                        .bookingTime(booking.getBookingTime())
                        .build());

                return enrichBookingResponse(booking, eventDetails);
            } else {
                booking.setPaymentStatus(PaymentStatus.FAILED);
                bookingRepository.save(booking);
                throw new PaymentFailedException("Payment failed for booking ID: " + booking.getId() + ". Message: " + paymentResponse.getMessage());
            }
        } catch (PaymentFailedException e) {

            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);
            throw e;
        } catch (Exception e) {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);
            log.error("Error during ticket booking for event {}: {}", request.getEventId(), e.getMessage());
            throw new RuntimeException("An error occurred during booking: " + e.getMessage(), e);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponse cancelBooking(Long bookingId, String jwtToken) {
        Long userId = getUserIdFromSecurityContext();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUserId().equals(userId) && !hasRole("ROLE_ADMIN")) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to cancel this booking.");
        }

        if (booking.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("Booking is already cancelled/refunded.");
        }
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalArgumentException("Only paid bookings can be cancelled.");
        }

        Map<String, Integer> incrementRequest = new HashMap<>();
        incrementRequest.put("numberOfTickets", booking.getNumberOfTickets());
        eventServiceClient.incrementEventTickets(jwtToken, booking.getEventId(), incrementRequest);

        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        booking.setCancellationTime(LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);

        kafkaProducerService.sendTicketCancelledEvent(TicketCancelledEvent.builder()
                .bookingId(updatedBooking.getId())
                .eventId(updatedBooking.getEventId())
                .userId(updatedBooking.getUserId())
                .numberOfTickets(updatedBooking.getNumberOfTickets())
                .cancellationTime(updatedBooking.getCancellationTime())
                .build());

        EventDetailsDto eventDetails = eventServiceClient.getEventById(jwtToken, updatedBooking.getEventId());
        return enrichBookingResponse(updatedBooking, eventDetails);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(String jwtToken) {
        Long userId = getUserIdFromSecurityContext();
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(booking -> {
                    EventDetailsDto eventDetails = eventServiceClient.getEventById(jwtToken, booking.getEventId());
                    return enrichBookingResponse(booking, eventDetails);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, String jwtToken) {
        Long userId = getUserIdFromSecurityContext();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUserId().equals(userId) && !hasRole("ROLE_ADMIN")) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view this booking.");
        }
        EventDetailsDto eventDetails = eventServiceClient.getEventById(jwtToken, booking.getEventId());
        return enrichBookingResponse(booking, eventDetails);
    }

    private BookingResponse enrichBookingResponse(Booking booking, EventDetailsDto eventDetails) {
        BookingResponse response = bookingMapper.toDto(booking);
        if (eventDetails != null) {
            response.setEventTitle(eventDetails.getTitle());
        }
        return response;
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}