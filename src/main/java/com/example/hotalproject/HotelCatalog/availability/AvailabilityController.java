package com.example.hotalproject.HotelCatalog.availability;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Availability and pricing check")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/check")
    @Operation(summary = "Check room availability by date range and guests with pricing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Availability result"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    public ResponseEntity<AvailabilityCheckResponse> checkAvailability(
            @Valid @RequestBody AvailabilityCheckRequest request
    ) {
        return ResponseEntity.ok(availabilityService.checkAvailability(request));
    }
}