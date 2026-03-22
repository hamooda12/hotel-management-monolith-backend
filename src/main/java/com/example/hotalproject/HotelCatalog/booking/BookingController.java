package com.example.hotalproject.HotelCatalog.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Create booking -> PENDING
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }
    @GetMapping ("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBooking(@Valid @PathVariable Long id) {
        return bookingService.getBooking(id);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getBookings() {
        return bookingService.getAllBookings();
    }
    @GetMapping("/room-types/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getBookingForRoomType(@Valid @PathVariable Long id) {
return  bookingService.getBookingWithRoomType(id);
    }

    // Confirm booking -> CONFIRMED
    @PatchMapping("/{bookingId}/confirm")
    public BookingResponse confirmBooking(@PathVariable Long bookingId) {
        return bookingService.confirmBooking(bookingId);
    }

    // Cancel booking
    @PatchMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    // Booking history for guest
    @GetMapping("/guest-history")
    public List<BookingResponse> getGuestHistory(@RequestParam String guestEmail) {
        return bookingService.getGuestBookingHistory(guestEmail);
    }

    // Upcoming bookings for manager
    @GetMapping("/manager-upcoming")
    public List<BookingResponse> getManagerUpcoming(@RequestParam String managerEmail) {
        return bookingService.getManagerUpcomingBookings(managerEmail);
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingException(BookingException ex) {
        return Map.of("error", ex.getMessage());
    }
}