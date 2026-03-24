package com.example.hotalproject.HotelCatalog.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @Email(message = "Guest email must be valid")
    private String guestEmail;

    @NotNull(message = "Room type ID is required")
    private Long roomTypeId;



    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    @Min(value = 1, message = "At least 1 guest is required")
    private int guests;
}

