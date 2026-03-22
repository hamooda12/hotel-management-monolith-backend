package com.example.hotalproject.HotelCatalog.Availability;

import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

	private final AvailabilityRepository availabilityRepository;
	private final RoomTypeService roomTypeService;
	@Override
	public AvailabilityCheckResponse checkAvailability(
			Long hotelId,
			Long roomTypeId,
			LocalDate checkinDate,
			LocalDate checkoutDate
	) {
		log.info("Checking availability for hotelId {} roomTypeId {} from {} to {}",
				hotelId, roomTypeId, checkinDate, checkoutDate);

		if (checkinDate == null || checkoutDate == null) {
			throw new IllegalArgumentException("checkinDate and checkoutDate are required");
		}

		if (!checkinDate.isBefore(checkoutDate)) {
			throw new IllegalArgumentException("checkinDate must be before checkoutDate");
		}

		RoomType roomType = roomTypeService.getRoomType(roomTypeId)
				.orElseThrow(() -> new EntityNotFoundException("Room type not found"));

		List<Availability> availabilities = availabilityRepository.findAll(
				AvailabilitySpecification.filterAvailabilities(
						hotelId,
						roomTypeId,
						checkinDate,
						checkoutDate
				)
		);

		List<AvailabilityResponse> records = availabilities.stream()
				.map(a -> new AvailabilityResponse(
						a.getId(),
						a.getDate(),
						a.getAvailableRooms(),
						a.getHotel().getId(),
						a.getRoomType().getId()
				))
				.toList();

		boolean available = true;

		for (LocalDate date = checkinDate; date.isBefore(checkoutDate); date = date.plusDays(1)) {
			LocalDate currentDate = date;

			Availability dayAvailability = availabilities.stream()
					.filter(a -> a.getDate().equals(currentDate))
					.findFirst()
					.orElse(null);

			int availableRoomsForDay = (dayAvailability != null)
					? dayAvailability.getAvailableRooms()
					: roomType.getTotalRooms();

			if (availableRoomsForDay <= 0) {
				available = false;
				break;
			}
		}

		return new AvailabilityCheckResponse(
				hotelId,
				roomTypeId,
				checkinDate,
				checkoutDate,
				available,
				records
		);
	}
}