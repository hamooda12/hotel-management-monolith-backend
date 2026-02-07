package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.room.Room;

import java.util.List;

public class RoomTypeServiceImpl implements  RoomTypeService {

    @Override
    public RoomType createRoomType(RoomType roomType) {
        return null;
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return null;
    }

    @Override
    public RoomType updateRoomType(Long id, RoomType roomType) {
        return null;
    }

    @Override
    public RoomType deleteRoomType(Long id) {
        return null;
    }

    @Override
    public RoomType getRoomTypeByName(String name) {
        return null;
    }

    @Override
    public List<RoomType> getRoomTypeByHotelId(Long hotelId) {
        return List.of();
    }

    @Override
    public RoomType getRoomTypeByHotelIdAndName(Long hotelId, String name) {
        return null;
    }

    @Override
    public RoomType replaceRoomType(Long id, RoomType roomType) {
        return null;
    }

    @Override
    public RoomType addRoomToRoomType(Long roomTypeId, Room room) {
        return null;
    }

    @Override
    public RoomType removeRoomFromRoomType(Long roomTypeId, Long roomId) {
        return null;
    }

    @Override
    public RoomType replaceRoomInRoomType(Long roomTypeId, Long oldRoomId, Long newRoomId) {
        return null;
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return List.of();
    }
}
