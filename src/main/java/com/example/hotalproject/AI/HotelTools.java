package com.example.hotalproject.AI;

import com.example.hotalproject.HotelCatalog.availability.AvailabilityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HotelTools {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HotelTools.class);

    private final AvailabilityServiceImpl availabilityService;

    public HotelTools(AvailabilityServiceImpl availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Tool(
            name = "checkRoomAvailability",
            description = "Check whether a hotel room is available on a specific check-in date."
    )
    public String checkRoomAvailability(
            int roomId,
            LocalDate checkinDate
    ) {

        LOGGER.info(
                "Checking availability for room: {}, checkinDate: {}",
                roomId,
                checkinDate
        );

        return availabilityService.isAvailable(
                roomId,
                checkinDate
        );
    }
}