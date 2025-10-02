package com.event.management.ticketingservice.repository;

import com.event.management.ticketingservice.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Booking> findById(Long id);

    @Query("SELECT SUM(b.numberOfTickets) FROM Booking b WHERE b.eventId = :eventId AND b.paymentStatus = 'PAID'")
    Optional<Integer> findTotalPaidTicketsForEvent(@Param("eventId") Long eventId);
}