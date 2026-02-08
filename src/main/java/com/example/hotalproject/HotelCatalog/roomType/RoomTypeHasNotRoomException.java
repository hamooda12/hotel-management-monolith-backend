package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseHasNotResourseException;

public class RoomTypeHasNotRoomException extends ResourseHasNotResourseException {
    public RoomTypeHasNotRoomException(String roomTypeName) {
        super("Room type '" + roomTypeName + "' has no rooms.");
    }
}
