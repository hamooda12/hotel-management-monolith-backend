package com.example.hotalproject.HotelCatalog.Availability;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/availability")
@RequiredArgsConstructor
public class AvailabilityController {

	private final AvailabilityService availabilityService;

	@GetMapping("/check")
	public ResponseEntity<AvailabilityCheckResponse> checkAvailability(
			@RequestParam Long hotelId,
			@RequestParam Long roomTypeId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkoutDate
	) {
		return ResponseEntity.ok(
				availabilityService.checkAvailability(hotelId, roomTypeId, checkinDate, checkoutDate)
		);
	}
}