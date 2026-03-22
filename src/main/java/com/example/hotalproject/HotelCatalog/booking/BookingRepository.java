package com.example.hotalproject.HotelCatalog.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

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

    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.roomType.id = :roomTypeId
              AND b.status <> 'CANCELLED'
              AND b.checkIn <= :date
              AND b.checkOut > :date
            """)
    int countActiveBookingsForDate(@Param("roomTypeId") Long roomTypeId,
                                   @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"roomType", "roomType.hotel"})
    List<Booking> findByGuestEmailOrderByCreatedAtDesc(String guestEmail);

    @EntityGraph(attributePaths = {"roomType", "roomType.hotel"})
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

    @EntityGraph(attributePaths = {"roomType", "roomType.hotel"})
    Optional<Booking> findWithRoomTypeById(Long id);

    List<Booking> findAllByRoomTypeId(Long roomTypeId);
}