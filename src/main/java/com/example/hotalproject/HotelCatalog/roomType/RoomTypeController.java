package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.BusinessValidationException;
import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ResourceNotFoundException;
import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.hotel.HotelService;
import com.example.hotalproject.HotelCatalog.hotel.HotelServiceImpl;
import com.example.hotalproject.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room Types", description = "Room type management")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;
    private final HotelServiceImpl hotelService;


    @GetMapping("/api/room-types")
    @Operation(summary = "Browse room types with filters and pagination")
    public ResponseEntity<PagedResponse<RoomTypeResponseDto>> browseRoomTypes(
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            Pageable pageable,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) Integer minTotalRooms,
            @RequestParam(required = false) Integer maxTotalRooms,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            HttpServletRequest request
    ) {

        Set<String> allowedParams = Set.of(
                "page", "size", "sort",
                "amenities", "nameContains",
                "minCapacity", "maxCapacity",
                "minTotalRooms", "maxTotalRooms",
                "minPrice", "maxPrice"
        );

        for (String paramName : request.getParameterMap().keySet()) {
            if (!allowedParams.contains(paramName)) {
                throw new BusinessValidationException(
                        "Query parameter '" + paramName + "' is not allowed"
                );
            }
        }

        Set<String> allowedSortFields = Set.of(
                "id", "name", "capacity", "basePrice", "totalRooms"
        );

        for (var order : pageable.getSort()) {
            if (!allowedSortFields.contains(order.getProperty())) {
                throw new BusinessValidationException(
                        "Sorting by '" + order.getProperty() + "' is not allowed"
                );
            }
        }

        return ResponseEntity.ok(
                roomTypeService.listRoomType(
                        pageable,
                        nameContains,
                        amenities,
                        minCapacity,
                        maxCapacity,
                        minTotalRooms,
                        maxTotalRooms,
                        minPrice,
                        maxPrice
                )
        );
    }

    @PostMapping("/api/hotels/{hotelId}/room-types")
    @Operation(summary = "Create a new room type for a specific hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room type created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<RoomTypeResponseDto> createRoomType(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomTypeRequestDto request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomTypeService.createRoomType(hotelId, request));
    }

    @PutMapping("/api/room-types/{id}")
    @Operation(summary = "Update an existing room type")
    public ResponseEntity<RoomTypeResponseDto> updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequestDto request
    ) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, request));
    }

    @GetMapping("/api/room-types/{id}")
    @Operation(summary = "Get a room type by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room type details"),
            @ApiResponse(responseCode = "404", description = "Room type not found")
    })
    public ResponseEntity<RoomTypeResponseDto> getRoomType(@PathVariable Long id) {
        RoomType room = roomTypeService.getRoomType(id)
                .orElseThrow(() -> new RoomTypeNotFoundException("Room type not found with id: " + id));
        return ResponseEntity.ok(RoomTypeMapper.toResponse(room));
    }

    @GetMapping("/api/hotels/{hotelId}/room-types")
    @Operation(summary = "Get all room types for a specific hotel")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByHotel(@PathVariable Long hotelId) {
        Hotel hotel = hotelService.getHotelByHotelId(hotelId).orElseThrow(()->new ResourceNotFoundException("There is no hotel with id: " + hotelId));

        return ResponseEntity.ok(roomTypeService.getRoomTypesByHotel(hotelId));
    }

    @DeleteMapping("/api/room-types/{id}")
    @Operation(summary = "Delete a room type")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/room-types/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or replace room type main image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room type image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing image file"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Room type not found"),
            @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
    public ResponseEntity<RoomTypeResponseDto> uploadRoomTypeImage(
            @PathVariable Long id,
            @Parameter(description = "Image file to upload", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(roomTypeService.uploadRoomTypeImage(id, file));
    }
}