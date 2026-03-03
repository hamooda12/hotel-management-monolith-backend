package com.example.hotalproject.HotelCatalog.Payment;


import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.booking.Booking;
import com.example.hotalproject.HotelCatalog.booking.BookingRepository;
import com.example.hotalproject.HotelCatalog.booking.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse createPaymentIntent(PaymentIntentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", request.getBookingId()));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Payment can only be initiated for PENDING bookings. Current status: " + booking.getStatus());
        }

        // Check if a payment already exists for this booking
        paymentRepository.findByBookingId(booking.getId()).ifPresent(existing -> {
            throw new ConflictException("Payment already exists for booking " + booking.getId() + " with status: " + existing.getStatus());
        });

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.INITIATED)
                .providerRef("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment {} initiated for booking {} – amount {}", payment.getId(), booking.getId(), payment.getAmount());

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse simulatePayment(Long paymentId, PaymentSimulateRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new ConflictException("Only INITIATED payments can be simulated. Current status: " + payment.getStatus());
        }

        PaymentStatus outcome = PaymentStatus.valueOf(request.getOutcome());
        payment.setStatus(outcome);
        payment = paymentRepository.save(payment);
        log.info("Payment {} simulated with outcome: {}", paymentId, outcome);

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new ConflictException("Only SUCCESS payments can be refunded. Current status: " + payment.getStatus());
        }

        // Verify the booking allows cancellation (booking must be cancelled or cancellable)
        Booking booking = payment.getBooking();
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new ConflictException("Refund is only allowed for cancelled bookings");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        log.info("Payment {} refunded", paymentId);

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .providerRef(payment.getProviderRef())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

