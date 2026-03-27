package com.example.hotalproject.HotelCatalog.payment;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.booking.Booking;
import com.example.hotalproject.HotelCatalog.booking.BookingRepository;
import com.example.hotalproject.HotelCatalog.booking.BookingStatus;
import com.example.hotalproject.HotelCatalog.notification.NotificationService;
import com.example.hotalproject.HotelCatalog.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Transactional
    public PaymentResponse createPaymentIntent(PaymentIntentRequest request, String requesterEmail, boolean privilegedUser) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", request.getBookingId()));
        ensureCanAccessBooking(booking, requesterEmail, privilegedUser);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException(
                    "Payment can only be initiated for PENDING bookings. Current status: " + booking.getStatus()
            );
        }


        paymentRepository.findByBookingId(booking.getId()).ifPresent(existing -> {
            throw new ConflictException(
                    "Payment already exists for booking " + booking.getId() +
                            " with status: " + existing.getStatus()
            );
        });

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.INITIATED)
                .providerRef("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        payment = paymentRepository.save(payment);

        notificationService.send(
                booking.getGuestEmail(),
                NotificationType.PAYMENT_INITIATED,
                "Payment initiated",
                "Payment intent created for booking #" + booking.getId() +
                        ". Reference: " + payment.getProviderRef()
        );

        log.info("Payment {} initiated for booking {} - amount {}",
                payment.getId(), booking.getId(), payment.getAmount());

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse simulatePayment(Long paymentId, PaymentSimulateRequest request, String requesterEmail, boolean privilegedUser) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        ensureCanAccessBooking(payment.getBooking(), requesterEmail, privilegedUser);

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new ConflictException(
                    "Only INITIATED payments can be simulated. Current status: " + payment.getStatus()
            );
        }

        PaymentStatus outcome = PaymentStatus.valueOf(request.getOutcome());
        Booking booking = payment.getBooking();

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException(
                    "Payment simulation requires booking to be PENDING. Current booking status: " + booking.getStatus()
            );
        }

        if (outcome == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);

            notificationService.send(
                    booking.getGuestEmail(),
                    NotificationType.PAYMENT_SUCCESS,
                    "Payment successful",
                    "Payment for booking #" + booking.getId() + " was successful."
            );
        }

        if (outcome == PaymentStatus.FAILED) {
            booking.setStatus(BookingStatus.PENDING);

            notificationService.send(
                    booking.getGuestEmail(),
                    NotificationType.PAYMENT_FAILED,
                    "Payment failed",
                    "Payment for booking #" + booking.getId() + " has failed."
            );
        }

        payment.setStatus(outcome);

        bookingRepository.save(booking);
        payment = paymentRepository.save(payment);

        log.info("Payment {} simulated with outcome {} for booking {}",
                paymentId, outcome, booking.getId());

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId, String requesterEmail, boolean privilegedUser) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        ensureCanAccessBooking(payment.getBooking(), requesterEmail, privilegedUser);

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new ConflictException(
                    "Only SUCCESS payments can be refunded. Current status: " + payment.getStatus()
            );
        }

        Booking booking = payment.getBooking();

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new ConflictException("Refund is only allowed for cancelled bookings");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        notificationService.send(
                booking.getGuestEmail(),
                NotificationType.PAYMENT_REFUNDED,
                "Payment refunded",
                "Payment for booking #" + booking.getId() + " has been refunded."
        );

        log.info("Payment {} refunded for booking {}", paymentId, booking.getId());

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
    public   PaymentResponse getPayment(Long paymentId, String requesterEmail, boolean privilegedUser) {
        Payment paymentResponse = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        ensureCanAccessBooking(paymentResponse.getBooking(), requesterEmail, privilegedUser);
        return  this.toResponse(paymentResponse);
    }
    public List<PaymentResponse> getPayments(String requesterEmail, boolean privilegedUser) {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .filter(payment -> privilegedUser || payment.getBooking().getGuestEmail().equalsIgnoreCase(requesterEmail))
                .map(this::toResponse)
                .toList();
    }

    private void ensureCanAccessBooking(Booking booking, String requesterEmail, boolean privilegedUser) {
        if (!privilegedUser && !booking.getGuestEmail().equalsIgnoreCase(requesterEmail)) {
            throw new AccessDeniedException("You are not allowed to access this payment");
        }
    }
}