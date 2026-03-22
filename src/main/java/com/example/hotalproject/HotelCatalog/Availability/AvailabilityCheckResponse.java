package com.example.hotalproject.HotelCatalog.Availability;

import java.time.LocalDate;
import java.util.List;

public record AvailabilityCheckResponse(
		Long roomTypeId,
		Long hotelId,
		LocalDate checkinDate,
		LocalDate checkoutDate,
		boolean available,
		List<AvailabilityResponse> records
) {}