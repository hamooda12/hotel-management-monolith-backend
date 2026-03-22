package com.example.hotalproject.HotelCatalog.Availability;

import java.time.LocalDate;

public interface AvailabilityService {
	AvailabilityCheckResponse checkAvailability(Long hotelId, Long roomTypeId, LocalDate checkinDate, LocalDate checkoutDate);
}