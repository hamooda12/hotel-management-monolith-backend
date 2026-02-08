package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseHasResourseException;

public class HotelHasRoomsException extends ResourseHasResourseException {
    public HotelHasRoomsException(String name) {
        super("Hotel '" + name + "' has existing rooms and cannot be deleted until all rooms are removed.");
    }

}
