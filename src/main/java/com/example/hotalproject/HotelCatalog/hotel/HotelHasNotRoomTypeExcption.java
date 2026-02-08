package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseHasNotResourseException;

public class HotelHasNotRoomTypeExcption extends ResourseHasNotResourseException {
    public HotelHasNotRoomTypeExcption(String roomTypeName) {
        super("There is no room type named '" + roomTypeName + "' in this hotel.");
    }
}
