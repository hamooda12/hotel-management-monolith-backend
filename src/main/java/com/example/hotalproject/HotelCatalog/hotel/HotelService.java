package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.CrudService;

public interface HotelService extends CrudService<Hotel, Long> {

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
