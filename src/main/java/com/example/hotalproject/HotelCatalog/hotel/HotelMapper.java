package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.roomType.RoomTypeResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelMapper {

    static public Hotel toEntity(HotelRequestDto request) {
        return Hotel.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .description(request.getDescription())
                .managerEmail(request.getManagerEmail())
                .build();
    }

    static public void updateEntity(Hotel hotel, HotelRequestDto request) {
        hotel.setName(request.getName());
        hotel.setCity(request.getCity());
        hotel.setAddress(request.getAddress());
        hotel.setDescription(request.getDescription());
        hotel.setManagerEmail(request.getManagerEmail());
    }

    static  public HotelResponseDto toResponse(Hotel hotel) {
        return toResponse(hotel, null);
    }

    static  public HotelResponseDto toResponse(Hotel hotel, List<RoomTypeResponseDto> roomTypes) {
        return HotelResponseDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .managerEmail(hotel.getManagerEmail())
                .imageUrl(hotel.getImageUrl())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .roomTypes(roomTypes)
                .build();
    }
}

