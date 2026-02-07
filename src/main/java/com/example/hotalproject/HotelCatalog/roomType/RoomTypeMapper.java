package com.example.hotalproject.HotelCatalog.roomType;

public class RoomTypeMapper {
    private RoomTypeMapper() {}

    public static RoomTypeResponseDto toDTO(RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        //int capacityAdults, int capacityChildren,
        //                               int maxGuests, Double basePrice, int totalInventory,
        //                               String amenities, String hotelName
        return new RoomTypeResponseDto(
                roomType.getCapacityAdults(),
                roomType.getCapacityChildren(),
                roomType.getMaxGuests(),
                roomType.getBasePrice(),
                roomType.getTotalInventory(),
                roomType.getAmenities(),
                roomType.getHotel().getName()
        );
    }

    public static RoomType toEntity(RoomTypeRequestDto dto) {
        if (dto == null) {
            return null;
        }
        RoomType roomType = new RoomType();
        roomType.setId(dto.getId());
        roomType.setName(dto.getName());
        return roomType;
    }
}