package com.example.hotalproject.HotelCatalog.room;

import java.util.List;

public interface RoomService {
    Room createRoom(Room room);
    Room getRoomById(Long id);
    Room updateRoom(Long id, Room room);
    void deleteRoom(Long id);
    Room replaceRoom(Long id, Room room);
    List<Room> getAllRooms();
    List<Room> getRoomsByHotelId(Long hotelId);
    List<Room> getRoomsByRoomTypeId(Long roomTypeId);

}
