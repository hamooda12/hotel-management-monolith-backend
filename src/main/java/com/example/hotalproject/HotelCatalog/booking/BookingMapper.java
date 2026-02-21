package com.example.hotalproject.HotelCatalog.booking;

import com.example.tourismbooking.booking.dto.BookingResponse;
import com.example.tourismbooking.booking.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .guestEmail(booking.getGuestEmail())
                .roomTypeId(booking.getRoomType().getId())
                .roomTypeName(booking.getRoomType().getName())
                .hotelId(booking.getRoomType().getHotel().getId())
                .hotelName(booking.getRoomType().getHotel().getName())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .guests(booking.getGuests())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

