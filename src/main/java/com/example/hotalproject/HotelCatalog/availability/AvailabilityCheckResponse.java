package com.example.hotalproject.HotelCatalog.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AvailabilityCheckResponse {
	private Long hotelId;
	private Long roomTypeId;
	private Integer guests;
	private LocalDate checkinDate;
	private LocalDate checkoutDate;
	private Boolean available;
	private BigDecimal totalPrice;
	private List<AvailabilityDayResponse> days;
}