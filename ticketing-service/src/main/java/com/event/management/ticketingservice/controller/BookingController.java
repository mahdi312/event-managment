package com.event.management.ticketingservice.controller;

import com.event.management.ticketingservice.dto.BookingRequest;
import com.event.management.ticketingservice.dto.BookingResponse;
import com.event.management.ticketingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Ticket booking and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Book tickets for an event", description = "Books tickets for a specified event. Requires authenticated USER.")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> bookTickets(@Valid @RequestBody BookingRequest request,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        BookingResponse response = bookingService.bookTickets(request, authorizationHeader);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel an existing booking", description = "Cancels a previously made booking and refunds tickets. Requires authenticated USER (for own bookings) or ADMIN.")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id,
                                                         @RequestHeader("Authorization") String authorizationHeader) {
        BookingResponse response = bookingService.cancelBooking(id, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all bookings for the authenticated user", description = "Retrieves a list of all bookings made by the authenticated user.")
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestHeader("Authorization") String authorizationHeader) {
        List<BookingResponse> bookings = bookingService.getBookingsByUserId(authorizationHeader);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get booking by ID", description = "Retrieves details of a specific booking by its ID. Requires authenticated USER (for own booking) or ADMIN.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        BookingResponse booking = bookingService.getBookingById(id, authorizationHeader);
        return ResponseEntity.ok(booking);
    }
}