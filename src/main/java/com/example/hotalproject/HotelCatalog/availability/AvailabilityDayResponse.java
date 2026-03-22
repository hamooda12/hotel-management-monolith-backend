package com.example.hotalproject.HotelCatalog.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class AvailabilityDayResponse {
	private LocalDate date;
	private Integer availableRooms;
	private BigDecimal price;
}