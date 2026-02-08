package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseAlreadyExsitInResourseException;

public class RoomTypeAlreadyExistInHotelException extends ResourseAlreadyExsitInResourseException {
    public RoomTypeAlreadyExistInHotelException(String roomTypeName) {
        super("The room type ' "+roomTypeName +"' is already available for this hotel. Choose a different name.");
    }
}
