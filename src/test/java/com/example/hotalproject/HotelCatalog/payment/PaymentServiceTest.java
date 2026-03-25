package com.example.hotalproject.HotelCatalog.payment;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.HotelCatalog.booking.Booking;
import com.example.hotalproject.HotelCatalog.booking.BookingRepository;
import com.example.hotalproject.HotelCatalog.booking.BookingStatus;
import com.example.hotalproject.HotelCatalog.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentIntent_shouldCreateInitiatedPayment() {
        Booking booking = Booking.builder()
                .id(5L)
                .guestEmail("guest@test.com")
                .status(BookingStatus.PENDING)
                .totalPrice(new BigDecimal("220.00"))
                .build();
        PaymentIntentRequest request = PaymentIntentRequest.builder().bookingId(5L).build();
        Payment saved = Payment.builder().id(3L).booking(booking).amount(new BigDecimal("220.00")).status(PaymentStatus.INITIATED).providerRef("SIM-AAAA").build();

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(5L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponse response = paymentService.createPaymentIntent(request, "guest@test.com", false);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(response.getBookingId()).isEqualTo(5L);
    }

    @Test
    void createPaymentIntent_shouldRejectNonOwnerGuest() {
        Booking booking = Booking.builder()
                .id(5L)
                .guestEmail("owner@test.com")
                .status(BookingStatus.PENDING)
                .totalPrice(new BigDecimal("220.00"))
                .build();
        PaymentIntentRequest request = PaymentIntentRequest.builder().bookingId(5L).build();
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.createPaymentIntent(request, "other@test.com", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void simulatePayment_shouldRejectWhenPaymentNotInitiated() {
        Booking booking = Booking.builder().id(7L).guestEmail("guest@test.com").status(BookingStatus.PENDING).build();
        Payment payment = Payment.builder().id(9L).booking(booking).status(PaymentStatus.SUCCESS).build();
        PaymentSimulateRequest request = PaymentSimulateRequest.builder().outcome("FAILED").build();

        when(paymentRepository.findById(9L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.simulatePayment(9L, request, "guest@test.com", false))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Only INITIATED");
    }
}
