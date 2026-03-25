package com.example.hotalproject.HotelCatalog.booking;

import com.example.hotalproject.HotelCatalog.notification.NotificationService;
import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.payment.Payment;
import com.example.hotalproject.HotelCatalog.payment.PaymentRepository;
import com.example.hotalproject.HotelCatalog.payment.PaymentStatus;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_shouldCreatePendingBookingForGuest() {
        Hotel hotel = Hotel.builder().id(3L).name("Hotel").city("City").address("Addr").managerEmail("m@test.com").build();
        RoomType roomType = RoomType.builder()
                .id(7L)
                .hotel(hotel)
                .capacity(3)
                .totalRooms(4)
                .basePrice(new BigDecimal("150.00"))
                .build();

        BookingRequest request = BookingRequest.builder()
                .roomTypeId(7L)
                .checkIn(LocalDate.now().plusDays(5))
                .checkOut(LocalDate.now().plusDays(7))
                .guests(2)
                .build();

        Booking saved = Booking.builder()
                .id(1L)
                .guestEmail("guest@test.com")
                .roomType(roomType)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(2)
                .status(BookingStatus.PENDING)
                .totalPrice(new BigDecimal("300.00"))
                .build();

        when(roomTypeRepository.findById(7L)).thenReturn(Optional.of(roomType));
        when(bookingRepository.countOverlappingActiveBookings(7L, request.getCheckIn(), request.getCheckOut())).thenReturn(0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
        when(bookingRepository.findWithRoomTypeById(1L)).thenReturn(Optional.of(saved));

        BookingResponse response = bookingService.createBooking(request, "guest@test.com", false);

        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("300.00");
    }

    @Test
    void createBooking_shouldRejectWhenNoAvailableRooms() {
        Hotel hotel = Hotel.builder().id(3L).name("Hotel").city("City").address("Addr").managerEmail("m@test.com").build();
        RoomType roomType = RoomType.builder()
                .id(7L)
                .hotel(hotel)
                .capacity(2)
                .totalRooms(1)
                .basePrice(new BigDecimal("100.00"))
                .build();
        BookingRequest request = BookingRequest.builder()
                .roomTypeId(7L)
                .checkIn(LocalDate.now().plusDays(3))
                .checkOut(LocalDate.now().plusDays(5))
                .guests(2)
                .build();

        when(roomTypeRepository.findById(7L)).thenReturn(Optional.of(roomType));
        when(bookingRepository.countOverlappingActiveBookings(7L, request.getCheckIn(), request.getCheckOut())).thenReturn(1);

        assertThatThrownBy(() -> bookingService.createBooking(request, "guest@test.com", false))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("No rooms available");
    }

    @Test
    void getBooking_shouldForbidNonOwnerGuest() {
        Booking booking = Booking.builder().id(11L).guestEmail("owner@test.com").build();
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(11L, "other@test.com", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void cancelBooking_shouldMarkBookingCancelledAndPaymentRefunded() {
        Hotel hotel = Hotel.builder().id(4L).name("Hotel").city("City").address("Addr").managerEmail("m@test.com").build();
        RoomType roomType = RoomType.builder().id(2L).hotel(hotel).name("Deluxe").build();
        Booking booking = Booking.builder()
                .id(20L)
                .guestEmail("guest@test.com")
                .roomType(roomType)
                .checkIn(LocalDate.now().plusDays(5))
                .checkOut(LocalDate.now().plusDays(7))
                .status(BookingStatus.CONFIRMED)
                .build();
        Payment payment = Payment.builder().id(9L).booking(booking).status(PaymentStatus.SUCCESS).build();

        when(bookingRepository.findWithRoomTypeById(20L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentRepository.findByBookingId(20L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(20L, "guest@test.com", false);

        assertThat(response.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}
