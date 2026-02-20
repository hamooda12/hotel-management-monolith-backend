package com.example.hotalproject.HotelCatalog.roomType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types", description = "Room type management")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    @PostMapping
    @Operation(summary = "Create a new room type")
    public ResponseEntity<RoomTypeResponseDto> createRoomType(@Valid @RequestBody RoomTypeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeService.createRoomType(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room type")
    public ResponseEntity<RoomTypeResponseDto> updateRoomType(@PathVariable Long id,
                                                           @Valid @RequestBody RoomTypeRequestDto request) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room type by ID")
    public ResponseEntity<RoomTypeResponseDto> getRoomType(@PathVariable Long id) {
        return ResponseEntity.ok(roomTypeService.getRoomType(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all room types for a hotel")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomTypeService.getRoomTypesByHotel(hotelId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room type")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}

