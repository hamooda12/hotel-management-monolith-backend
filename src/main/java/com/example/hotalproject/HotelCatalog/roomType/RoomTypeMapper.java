package com.example.hotalproject.HotelCatalog.roomType;
import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeMapper {

    static public RoomType toEntity(RoomTypeRequestDto request, Hotel hotel) {
        return RoomType.builder()
                .hotel(hotel)
                .name(request.getName())
                .capacity(request.getCapacity())
                .basePrice(request.getBasePrice())
                .amenities(request.getAmenities())
                .totalRooms(request.getTotalRooms())
                .build();
    }

    static public void updateEntity(RoomType roomType, RoomTypeRequestDto request) {
        roomType.setName(request.getName());
        roomType.setCapacity(request.getCapacity());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setAmenities(request.getAmenities());
        roomType.setTotalRooms(request.getTotalRooms());
    }

    static  public RoomTypeResponseDto toResponse(RoomType roomType) {
        return RoomTypeResponseDto.builder()
                .id(roomType.getId())
                .hotelId(roomType.getHotel().getId())
                .hotelName(roomType.getHotel().getName())
                .name(roomType.getName())
                .capacity(roomType.getCapacity())
                .basePrice(roomType.getBasePrice())
                .amenities(roomType.getAmenities())
                .totalRooms(roomType.getTotalRooms())
                .imageUrl(roomType.getImageUrl())
                .build();
    }
}

