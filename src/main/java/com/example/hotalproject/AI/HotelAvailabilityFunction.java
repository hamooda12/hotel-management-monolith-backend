package com.example.hotalproject.AI;

import com.example.hotalproject.HotelCatalog.availability.AvailabilityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Function;

@Component("hotelAvailabilityFunction")
@Description("Checks whether a hotel room type is available on a specific check-in date.")
public class HotelAvailabilityFunction
        implements Function<HotelAvailabilityRequest, HotelAvailabilityResponse> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HotelAvailabilityFunction.class);

    private final AvailabilityServiceImpl availabilityService;

    public HotelAvailabilityFunction(AvailabilityServiceImpl availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Override
    public HotelAvailabilityResponse apply(HotelAvailabilityRequest request) {
        LOGGER.info(
                "Function tool: checking availability for roomTypeId: {}, checkinDate: {}",
                request.roomTypeId(),
                request.checkinDate()
        );

        String result = availabilityService.isAvailable(
                request.roomTypeId(),
                request.checkinDate()
        );

        return new HotelAvailabilityResponse(
                request.roomTypeId(),
                request.checkinDate(),
                result
        );
    }

    @Description("Request data for checking hotel room type availability.")
    public record HotelAvailabilityRequest(
            int roomTypeId,
            LocalDate checkinDate
    ) {
    }

    public record HotelAvailabilityResponse(
            int roomTypeId,
            LocalDate checkinDate,
            String availability
    ) {
    }
}
