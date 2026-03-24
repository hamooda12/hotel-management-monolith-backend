package com.example.hotalproject.HotelCatalog.roomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDto {

    private Long id;
    private Long hotelId;
    private String hotelName;
    private String name;
    private int capacity;
    private BigDecimal basePrice;
    private String amenities;
    private int totalRooms;
    private String imageUrl;
}

