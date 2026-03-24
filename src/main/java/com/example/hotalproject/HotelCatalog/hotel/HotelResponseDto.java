package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.roomType.RoomTypeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDto {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private String managerEmail;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoomTypeResponseDto> roomTypes;
}

