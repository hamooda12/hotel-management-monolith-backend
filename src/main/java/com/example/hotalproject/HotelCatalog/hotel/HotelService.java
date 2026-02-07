package com.example.hotalproject.HotelCatalog.hotel;

public interface HotelService {
    Hotel createHotel(Hotel hotel);
    Hotel getHotelById(Long id);
    Hotel updateHotel(Long id, Hotel hotel);
    Hotel deleteHotel(Long id);
    Hotel replaceHotel(Long id, Hotel hotel);
    Hotel getHotelByName(String name);
    Hotel getHotelByLocation(String location);
    Hotel getHotelByRating(Double rating);
    Hotel getHotelByPrice(Double price);
    Hotel getHotelByRoomType(String roomType);
    Hotel getAllHotels();
    Hotel addRoomTypeToHotel(Long hotelId, Long roomTypeId);
    Hotel removeRoomTypeFromHotel(Long hotelId, Long roomTypeId);
    Hotel replaceRoomTypeInHotel(Long hotelId, Long oldRoomTypeId, Long newRoomTypeId);
}
