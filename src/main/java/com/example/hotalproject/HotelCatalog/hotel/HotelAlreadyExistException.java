package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourseAlreadyExistException;

public class HotelAlreadyExistException extends ResourseAlreadyExistException {
    public HotelAlreadyExistException(Long id) {
        super("There is already a hotel with the same attribute and it's ID is"+id);
    }
}
