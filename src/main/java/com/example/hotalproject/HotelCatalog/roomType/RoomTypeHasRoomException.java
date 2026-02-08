package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseHasResourseException;

public class RoomTypeHasRoomException extends ResourseHasResourseException {
    public RoomTypeHasRoomException(String RoomTypeName) {
        super("Cannot delete room type '" + RoomTypeName + "' because it still has rooms.");
    }
}
