package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseAlreadyExistException;

public class RoomAlreadyExistInRoomType extends ResourseAlreadyExistException {
    public RoomAlreadyExistInRoomType(String roomNumber) {
        super("Room '" + roomNumber + "' already exists in this room type.");
    }
}
