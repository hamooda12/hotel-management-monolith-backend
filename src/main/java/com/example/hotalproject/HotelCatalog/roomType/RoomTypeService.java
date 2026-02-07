package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.room.Room;

import java.util.List;

public interface RoomTypeService {
    RoomType createRoomType(RoomType roomType);
        RoomType getRoomTypeById(Long id);
        RoomType updateRoomType(Long id, RoomType roomType);
        RoomType deleteRoomType(Long id);
        RoomType getRoomTypeByName(String name);
        List<RoomType> getRoomTypeByHotelId(Long hotelId);
        RoomType getRoomTypeByHotelIdAndName(Long hotelId, String name);
        RoomType replaceRoomType(Long id, RoomType roomType);
        RoomType addRoomToRoomType(Long roomTypeId, Room room);
        RoomType removeRoomFromRoomType(Long roomTypeId, Long roomId);
        RoomType replaceRoomInRoomType(Long roomTypeId, Long oldRoomId, Long newRoomId);
        List<RoomType> getAllRoomTypes();
}
