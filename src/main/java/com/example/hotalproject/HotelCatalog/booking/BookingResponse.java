package com.example.hotalproject.HotelCatalog.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private String guestEmail;
    private Long roomTypeId;
    private String roomTypeName;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}

