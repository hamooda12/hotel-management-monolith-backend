package com.example.hotalproject.HotelCatalog.booking;

import com.example.tourismbooking.booking.entity.Booking;
import com.example.tourismbooking.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Count overlapping active (non-cancelled) bookings for a given room type
     * within a date range. Two bookings overlap when:
     *   existing.checkIn < newCheckOut  AND  existing.checkOut > newCheckIn
     */
    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.roomType.id = :roomTypeId
              AND b.status <> 'CANCELLED'
              AND b.checkIn < :checkOut
              AND b.checkOut > :checkIn
            """)
    int countOverlappingActiveBookings(@Param("roomTypeId") Long roomTypeId,
                                       @Param("checkIn") LocalDate checkIn,
                                       @Param("checkOut") LocalDate checkOut);

    List<Booking> findByGuestEmailOrderByCreatedAtDesc(String guestEmail);

    @Query("""
            SELECT b FROM Booking b
            JOIN b.roomType rt
            JOIN rt.hotel h
            WHERE h.managerEmail = :managerEmail
              AND b.checkIn >= :fromDate
              AND b.status <> 'CANCELLED'
            ORDER BY b.checkIn ASC
            """)
    List<Booking> findUpcomingByManager(@Param("managerEmail") String managerEmail,
                                        @Param("fromDate") LocalDate fromDate);
}

