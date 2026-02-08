package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;

public class HotelNotFoundException extends ResourceNotFoundException {
    public HotelNotFoundException(Long id) {
        super("Hotel with id '" + id + "' not found.");
    }



}
