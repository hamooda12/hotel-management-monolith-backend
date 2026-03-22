package com.example.hotalproject.HotelCatalog.availability;

public interface AvailabilityService {
    AvailabilityCheckResponse checkAvailability(AvailabilityCheckRequest request);
}