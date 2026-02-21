package com.example.hotalproject.HotelCatalog.booking;


import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final PaymentRepository paymentRepository;
    private final PricingService pricingService;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    private static final int CANCELLATION_HOURS_BEFORE_CHECKIN = 48;

    /**
     * Create a new booking with availability check and price computation.
     * Uses @Transactional to ensure atomicity – the overlapping-booking count
     * and the insert happen inside the same transaction.
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1) Validate dates
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new BusinessException("Check-out date must be after check-in date", HttpStatus.BAD_REQUEST);
        }

        // 2) Load room type
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", request.getRoomTypeId()));

        // 3) Validate guest count
        if (request.getGuests() > roomType.getCapacity()) {
            throw new BusinessException(
                    "Guest count (" + request.getGuests() + ") exceeds room capacity (" + roomType.getCapacity() + ")",
                    HttpStatus.BAD_REQUEST);
        }

        // 4) Check availability (count overlapping active bookings)
        int overlapping = bookingRepository.countOverlappingActiveBookings(
                roomType.getId(), request.getCheckIn(), request.getCheckOut());
        if (overlapping >= roomType.getTotalRooms()) {
            throw new ConflictException("No rooms available for the selected dates");
        }

        // 5) Compute price
        PricingService.PricingResult pricingResult =
                pricingService.computePrice(roomType.getBasePrice(), request.getCheckIn(), request.getCheckOut());

        // 6) Create booking
        Booking booking = Booking.builder()
                .guestEmail(request.getGuestEmail())
                .roomType(roomType)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(request.getGuests())
                .status(BookingStatus.PENDING)
                .totalPrice(pricingResult.totalPrice())
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking {} created for guest {} – status PENDING", booking.getId(), booking.getGuestEmail());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Confirm a booking – requires a successful payment.
     */
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Only PENDING bookings can be confirmed. Current status: " + booking.getStatus());
        }

        // Check that a successful payment exists
        Optional<Payment> payment = paymentRepository.findByBookingId(bookingId);
        if (payment.isEmpty() || payment.get().getStatus() != PaymentStatus.SUCCESS) {
            throw new ConflictException("Payment must be successful before confirming the booking");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        log.info("Booking {} confirmed", bookingId);

        // Send confirmation notification
        notificationService.sendBookingConfirmed(booking);

        return bookingMapper.toResponse(booking);
    }

    /**
     * Cancel a booking – enforces cancellation policy (48h before check-in).
     * If a successful payment exists, marks it as REFUNDED.
     */
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled");
        }

        // Cancellation policy: at least 48 hours before check-in
        LocalDateTime cancellationDeadline = booking.getCheckIn()
                .atStartOfDay()
                .minusHours(CANCELLATION_HOURS_BEFORE_CHECKIN);
        if (LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new ConflictException(
                    "Cancellation is no longer allowed. Must cancel at least "
                            + CANCELLATION_HOURS_BEFORE_CHECKIN + " hours before check-in");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        log.info("Booking {} cancelled", bookingId);

        // Refund payment if it was successful
        paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                log.info("Payment {} refunded for booking {}", payment.getId(), bookingId);
            }
        });

        // Send cancellation notification
        notificationService.sendBookingCancelled(booking);

        return bookingMapper.toResponse(booking);
    }

    /**
     * Guest booking history.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getGuestBookings(String guestEmail) {
        return bookingRepository.findByGuestEmailOrderByCreatedAtDesc(guestEmail)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    /**
     * Manager upcoming bookings.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getManagerUpcomingBookings(String managerEmail, LocalDate fromDate) {
        if (fromDate == null) {
            fromDate = LocalDate.now();
        }
        return bookingRepository.findUpcomingByManager(managerEmail, fromDate)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }
}

