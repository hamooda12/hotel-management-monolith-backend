package com.example.hotalproject.HotelCatalog.Availability;

import java.time.LocalDate;

public record AvailabilityResponse(
		Long id,
		LocalDate date,
		Integer availableRooms,
		Long hotelId,
		Long roomTypeId
) {
}