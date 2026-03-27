package com.example.hotalproject.HotelCatalog.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.hotalproject.security.Role;
import com.example.hotalproject.security.SecurityUtils;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Booking lifecycle operations")
public class BookingController {

    private final BookingService bookingService;

    // Create booking -> PENDING
    @PostMapping
    @Operation(summary = "Create a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Validation/business error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return bookingService.createBooking(request, SecurityUtils.currentUserEmail(), privileged);
    }
    @GetMapping ("{id}")
    @Operation(summary = "Get booking by ID")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBooking(@Valid @PathVariable Long id) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return bookingService.getBooking(id, SecurityUtils.currentUserEmail(), privileged);
    }
    @GetMapping
    @Operation(summary = "Get all bookings (admin/manager)")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/room-types/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BookingResponse> getBookingForRoomType(@Valid @PathVariable Long id) {
        return  bookingService.getBookingWithRoomType(id);
    }



    // Cancel booking
    @PatchMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking")
    public BookingResponse cancelBooking(@PathVariable Long bookingId) {
        boolean privileged = SecurityUtils.hasRole(Role.ADMIN) || SecurityUtils.hasRole(Role.MANAGER);
        return bookingService.cancelBooking(bookingId, SecurityUtils.currentUserEmail(), privileged);
    }

    // Booking history for guest
    @GetMapping("/guest-history")
    @Operation(summary = "Get authenticated guest booking history")
    public List<BookingResponse> getGuestHistory() {
        return bookingService.getGuestBookingHistory(SecurityUtils.currentUserEmail());
    }

    // Upcoming bookings for manager
    @GetMapping("/manager-upcoming")
    @Operation(summary = "Get manager upcoming bookings (by authenticated manager)")
    public List<BookingResponse> getManagerUpcoming() {
        return bookingService.getManagerUpcomingBookings(SecurityUtils.currentUserEmail());
    }
}