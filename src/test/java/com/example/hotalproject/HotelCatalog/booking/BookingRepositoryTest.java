package com.example.hotalproject.HotelCatalog.booking;

import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    void countOverlappingActiveBookings_shouldCountOnlyOverlappingNonCancelledBookings() {
        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Test Hotel")
                .city("City")
                .address("Addr")
                .managerEmail("manager@test.com")
                .build());
        RoomType roomType = roomTypeRepository.save(RoomType.builder()
                .hotel(hotel)
                .name("Standard")
                .capacity(2)
                .basePrice(new BigDecimal("100.00"))
                .totalRooms(5)
                .build());

        bookingRepository.save(Booking.builder()
                .guestEmail("guest1@test.com")
                .roomType(roomType)
                .checkIn(LocalDate.of(2026, 6, 10))
                .checkOut(LocalDate.of(2026, 6, 13))
                .guests(2)
                .status(BookingStatus.CONFIRMED)
                .totalPrice(new BigDecimal("300.00"))
                .build());

        bookingRepository.save(Booking.builder()
                .guestEmail("guest2@test.com")
                .roomType(roomType)
                .checkIn(LocalDate.of(2026, 6, 10))
                .checkOut(LocalDate.of(2026, 6, 13))
                .guests(2)
                .status(BookingStatus.CANCELLED)
                .totalPrice(new BigDecimal("300.00"))
                .build());

        int overlapping = bookingRepository.countOverlappingActiveBookings(
                roomType.getId(),
                LocalDate.of(2026, 6, 11),
                LocalDate.of(2026, 6, 12)
        );

        assertThat(overlapping).isEqualTo(1);
    }
}
