package com.example.hotalproject.HotelCatalog.availability;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.booking.BookingRepository;
import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Test
    void checkAvailability_shouldReturnUnavailableWhenCapacityExceeded() {
        Hotel hotel = Hotel.builder().id(1L).name("Hotel").city("City").address("Addr").managerEmail("m@test.com").build();
        RoomType roomType = RoomType.builder()
                .id(2L)
                .hotel(hotel)
                .name("Deluxe")
                .capacity(2)
                .basePrice(new BigDecimal("100.00"))
                .totalRooms(3)
                .build();
        AvailabilityCheckRequest request = new AvailabilityCheckRequest();
        request.setHotelId(1L);
        request.setRoomTypeId(2L);
        request.setCheckinDate(LocalDate.now().plusDays(5));
        request.setCheckoutDate(LocalDate.now().plusDays(7));
        request.setGuests(3);

        when(roomTypeRepository.findById(2L)).thenReturn(Optional.of(roomType));
        when(bookingRepository.countActiveBookingsForDate(2L, request.getCheckinDate())).thenReturn(0);
        when(bookingRepository.countActiveBookingsForDate(2L, request.getCheckinDate().plusDays(1))).thenReturn(0);

        AvailabilityCheckResponse response = availabilityService.checkAvailability(request);

        assertThat(response.getAvailable()).isFalse();
        assertThat(response.getDays()).hasSize(2);
    }

    @Test
    void checkAvailability_shouldThrowConflictWhenRoomTypeNotInHotel() {
        Hotel otherHotel = Hotel.builder().id(9L).name("Other").city("City").address("Addr").managerEmail("m@test.com").build();
        RoomType roomType = RoomType.builder()
                .id(2L).hotel(otherHotel).capacity(2).basePrice(new BigDecimal("100.00")).totalRooms(3).name("Deluxe")
                .build();

        AvailabilityCheckRequest request = new AvailabilityCheckRequest();
        request.setHotelId(1L);
        request.setRoomTypeId(2L);
        request.setCheckinDate(LocalDate.now().plusDays(5));
        request.setCheckoutDate(LocalDate.now().plusDays(6));
        request.setGuests(2);

        when(roomTypeRepository.findById(2L)).thenReturn(Optional.of(roomType));

        assertThatThrownBy(() -> availabilityService.checkAvailability(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("does not belong");
    }
}
