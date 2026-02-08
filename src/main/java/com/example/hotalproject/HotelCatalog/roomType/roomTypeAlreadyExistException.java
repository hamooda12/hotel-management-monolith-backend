package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseAlreadyExistException;

public class roomTypeAlreadyExistException extends ResourseAlreadyExistException {
    public roomTypeAlreadyExistException(String roomTypeName) {
        super("Room type '" + roomTypeName + "' already exists in this hotel.");
    }
}
