package com.example.hotalproject.HotelCatalog.availability;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityCheckRequest {

	@NotNull
	private Long hotelId;

	@NotNull
	private Long roomTypeId;

	@NotNull
	private LocalDate checkinDate;

	@NotNull
	private LocalDate checkoutDate;

	@NotNull
	@Min(1)
	private Integer guests;
}