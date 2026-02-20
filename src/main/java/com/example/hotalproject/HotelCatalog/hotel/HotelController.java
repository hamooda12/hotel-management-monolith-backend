package com.example.hotalproject.HotelCatalog.hotel;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Hotel catalog management")
public class HotelController {

    private final HotelServiceImpl hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel")
    public ResponseEntity<HotelResponseDto> createHotel(@Valid @RequestBody HotelRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hotel")
    public ResponseEntity<HotelResponseDto> updateHotel(@PathVariable Long id,
                                                     @Valid @RequestBody HotelRequestDto request) {
        return ResponseEntity.ok(hotelService.updateHotel(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel details with room types")
    public ResponseEntity<HotelResponseDto> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotel(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hotel")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Browse hotels with filters and pagination")
    public ResponseEntity<Page<HotelResponseDto>> browseHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(hotelService.browseHotels(city, nameContains,
                minCapacity, minPrice, maxPrice, pageable));
    }
}

