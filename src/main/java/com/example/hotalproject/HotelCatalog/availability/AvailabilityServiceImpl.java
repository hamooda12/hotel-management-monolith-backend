package com.example.hotalproject.HotelCatalog.availability;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.booking.BookingRepository;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import com.example.hotalproject.HotelCatalog.roomType.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;

    @Override
    public AvailabilityCheckResponse checkAvailability(AvailabilityCheckRequest request) {
        validateDates(request.getCheckinDate(), request.getCheckoutDate());

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", request.getRoomTypeId()));

        if (!roomType.getHotel().getId().equals(request.getHotelId())) {
            throw new ConflictException("Room type does not belong to the given hotel");
        }

        boolean capacityOk = request.getGuests() <= roomType.getCapacity();
        boolean available = capacityOk;

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<AvailabilityDayResponse> days = new ArrayList<>();

        for (LocalDate date = request.getCheckinDate(); date.isBefore(request.getCheckoutDate()); date = date.plusDays(1)) {
            int activeBookings = bookingRepository.countActiveBookingsForDate(roomType.getId(), date);
            int availableRoomsForDay = roomType.getTotalRooms() - activeBookings;

            if (availableRoomsForDay < 0) {
                availableRoomsForDay = 0;
            }

            BigDecimal dailyPrice = calculateDailyPrice(roomType.getBasePrice(), date);

            if (availableRoomsForDay <= 0) {
                available = false;
            }

            totalPrice = totalPrice.add(dailyPrice);

            days.add(AvailabilityDayResponse.builder()
                    .date(date)
                    .availableRooms(availableRoomsForDay)
                    .price(dailyPrice)
                    .build());
        }

        if (!capacityOk) {
            available = false;
        }

        return AvailabilityCheckResponse.builder()
                .hotelId(request.getHotelId())
                .roomTypeId(request.getRoomTypeId())
                .guests(request.getGuests())
                .checkinDate(request.getCheckinDate())
                .checkoutDate(request.getCheckoutDate())
                .available(available)
                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                .days(days)
                .build();
    }

    private void validateDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate == null || checkoutDate == null) {
            throw new AvaliabilituyException("checkinDate and checkoutDate are required");
        }

        if (checkinDate.isBefore(LocalDate.now())) {
            throw new AvaliabilituyException("checkinDate cannot be in the past");
        }

        if (!checkinDate.isBefore(checkoutDate)) {
            throw new AvaliabilituyException("checkinDate must be before checkoutDate");
        }
    }

    private BigDecimal calculateDailyPrice(BigDecimal basePrice, LocalDate date) {
        BigDecimal multiplier = BigDecimal.ONE;

        // Weekend pricing example: Friday/Saturday +20%
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            multiplier = multiplier.multiply(BigDecimal.valueOf(1.20));
        }

        // Seasonal pricing example: June, July, August +25%
        if (date.getMonth() == Month.JUNE ||
                date.getMonth() == Month.JULY ||
                date.getMonth() == Month.AUGUST) {
            multiplier = multiplier.multiply(BigDecimal.valueOf(1.25));
        }

        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}