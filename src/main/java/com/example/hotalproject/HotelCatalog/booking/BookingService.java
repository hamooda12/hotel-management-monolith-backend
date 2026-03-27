package com.example.hotalproject.HotelCatalog.booking;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.notification.NotificationService;
import com.example.hotalproject.HotelCatalog.notification.NotificationType;
import com.example.hotalproject.HotelCatalog.payment.Payment;
import com.example.hotalproject.HotelCatalog.payment.PaymentRepository;
import com.example.hotalproject.HotelCatalog.payment.PaymentStatus;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import com.example.hotalproject.security.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private  final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;
    public BookingResponse getBooking(Long bookingId, String requesterEmail, boolean privilegedUser) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->new BookingException("Booking not found"));
        ensureCanAccessBooking(booking, requesterEmail, privilegedUser);
        return  BookingMapper.toResponse(booking);
    }
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponse> bookingResponseList = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponseList.add(BookingMapper.toResponse(booking));
        }
        return bookingResponseList;
    }
    public List<BookingResponse> getBookingWithRoomType(Long  roomTypeId) {
        if(roomTypeRepository.findById(roomTypeId).isPresent()) {
            if(bookingRepository.findAllByRoomTypeId(roomTypeId).isEmpty())
                throw  new RoomTypesWithoutBookings("THe room type with id " + roomTypeId + " is empty");
            else {
                return  bookingRepository.findAllByRoomTypeId(roomTypeId).stream().map(BookingMapper::toResponse).toList();
            }
        }
        else {
            throw new ResourceNotFoundException("Room Type not found");
        }
    }

    public BookingResponse createBooking(BookingRequest request, String requesterEmail, boolean privilegedUser) {
        validateBookingRequest(request, privilegedUser);

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", request.getRoomTypeId()));

        if (request.getGuests() > roomType.getCapacity()) {
            throw new BookingException("Number of guests exceeds room capacity");
        }

        int overlapping = bookingRepository.countOverlappingActiveBookings(
                roomType.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        int availableRooms = roomType.getTotalRooms() - overlapping;
        if (availableRooms <= 0) {
            throw new BookingException("No rooms available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalPrice = roomType.getBasePrice().multiply(BigDecimal.valueOf(nights));

        String guestEmail = privilegedUser ? request.getGuestEmail() : requesterEmail;
        Booking booking = Booking.builder()
                .guestEmail(guestEmail)
                .roomType(roomType)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(request.getGuests())
                .status(BookingStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        Booking saved = bookingRepository.save(booking);

        notificationService.send(
                saved.getGuestEmail(),
                NotificationType.BOOKING_CREATED,
                "Booking created",
                "Your booking #" + saved.getId() + " has been created and is pending payment."
        );

        Booking detailed = bookingRepository.findWithRoomTypeById(saved.getId())
                .orElseThrow(() -> new BookingException("Booking saved but could not be reloaded"));

        return BookingMapper.toResponse(detailed);
    }

    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findWithRoomTypeById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Cancelled booking cannot be confirmed");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new BookingException("Booking is already confirmed");
        }

        int overlapping = bookingRepository.countOverlappingActiveBookings(
                booking.getRoomType().getId(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );

        if (overlapping > booking.getRoomType().getTotalRooms()) {
            throw new BookingException("No rooms available anymore for confirmation");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updated = bookingRepository.save(booking);

        notificationService.send(
                updated.getGuestEmail(),
                NotificationType.BOOKING_CONFIRMED,
                "Booking confirmed",
                "Your booking #" + updated.getId() + " has been confirmed."
        );

        return BookingMapper.toResponse(updated);
    }

    public BookingResponse cancelBooking(Long bookingId, String requesterEmail, boolean privilegedUser) {
        Booking booking = bookingRepository.findWithRoomTypeById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        ensureCanAccessBooking(booking, requesterEmail, privilegedUser);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        LocalDate today = LocalDate.now();

        if (!today.isBefore(booking.getCheckIn().minusDays(1))) {
            throw new BookingException("Cancellation is not allowed less than 24 hours before check-in");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        Payment payment = paymentRepository.findByBookingId(updated.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking id: " + updated.getId()));
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        notificationService.send(
                updated.getGuestEmail(),
                NotificationType.BOOKING_CANCELLED,
                "Booking cancelled",
                "Your booking #" + updated.getId() + " has been cancelled successfully."
        );

        return BookingMapper.toResponse(updated);
    }

    @Transactional
    public List<BookingResponse> getGuestBookingHistory(String guestEmail) {
        if(!appUserRepository.existsByEmail(guestEmail)) {
            throw  new ResourceNotFoundException("There is no user with "+guestEmail);
        }
        if( bookingRepository.findByGuestEmailOrderByCreatedAtDesc(guestEmail).isEmpty())
            throw new BookingException("No bookings found for " + guestEmail);
        return bookingRepository.findByGuestEmailOrderByCreatedAtDesc(guestEmail)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<BookingResponse> getManagerUpcomingBookings(String managerEmail) {
        return bookingRepository.findUpcomingByManager(managerEmail, LocalDate.now())
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    private void validateBookingRequest(BookingRequest request, boolean privilegedUser) {
        if (request.getCheckIn() == null || request.getCheckOut() == null) {
            throw new BookingException("Check-in and check-out dates are required");
        }

        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new BookingException("Check-out date must be after check-in date");
        }

        if (request.getGuests() < 1) {
            throw new BookingException("At least one guest is required");
        }

        if (privilegedUser && (request.getGuestEmail() == null || request.getGuestEmail().isBlank())) {
            throw new BookingException("Guest email is required");
        }
    }

    private void ensureCanAccessBooking(Booking booking, String requesterEmail, boolean privilegedUser) {
        if (!privilegedUser && !booking.getGuestEmail().equalsIgnoreCase(requesterEmail)) {
            throw new AccessDeniedException("You are not allowed to access this booking");
        }
    }
}