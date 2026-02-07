package com.example.hotalproject.HotelCatalog.hotel;

public class HotelMapper {

    private HotelMapper() {}

    // تحويل من DTO إلى Entity
    public static Hotel toEntity(HotelRequestDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setRating(dto.getRating());
        hotel.setPhoneNumber(dto.getPhoneNumber());
        hotel.setDescription(dto.getDescription());
        return hotel;
    }

    // تحديث Entity موجود من DTO
    public static void updateEntity(Hotel hotel, HotelRequestDto dto) {
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setRating(dto.getRating());
        hotel.setPhoneNumber(dto.getPhoneNumber());
        hotel.setDescription(dto.getDescription());
    }

    // تحويل من Entity إلى Response DTO
    public static HotelResponseDto toDto(Hotel hotel) {
        return new HotelResponseDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getCity(),
                hotel.getCountry(),
                hotel.getRating(),
                hotel.getPhoneNumber(),
                hotel.getDescription()
        );
    }
}
