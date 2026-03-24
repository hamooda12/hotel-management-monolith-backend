package com.example.hotalproject.HotelCatalog.hotel;



import com.example.hotalproject.HotelCatalog.Utility.Exceptions.BusinessValidationException;
import com.example.hotalproject.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Hotel catalog management")
public class HotelController {

    private final HotelServiceImpl hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hotel created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel details"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelResponseDto> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotel(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hotel")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or replace hotel main image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing image file"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Hotel not found"),
            @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
    public ResponseEntity<HotelResponseDto> uploadHotelImage(
            @PathVariable Long id,
            @Parameter(description = "Image file to upload", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(hotelService.uploadHotelImage(id, file));
    }

    @GetMapping
    @Operation(summary = "Browse hotels with filters and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged hotel list"),
            @ApiResponse(responseCode = "400", description = "Invalid filter/sort values")
    })
    public ResponseEntity<PagedResponse<HotelResponseDto>> browseHotels(
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            Pageable pageable,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) LocalDate before,
            @RequestParam(required = false) LocalDate after,
            @RequestParam(required = false) String description,
            HttpServletRequest request
    ) {

        Set<String> ALLOWED_PARAMS = Set.of(
                "page", "size", "sort",
                "city", "nameContains",
                "before", "after",
                "description"
        );
        for (String paramName : request.getParameterMap().keySet()) {
            if (!ALLOWED_PARAMS.contains(paramName)) {
                throw new com.example.hotalproject.HotelCatalog.Utility.Exceptions.BusinessValidationException("Query parameter '" + paramName + "' is not allowed");
            }
        }
        LinkedList<String> ALLOWED = new LinkedList<>();
        ALLOWED.addAll(Arrays.asList("city", "name", "description","createdAt","address","id"));

        for (var order : pageable.getSort()) {
            if (!ALLOWED.contains(order.getProperty())) {
                throw new BusinessValidationException(
                        "Sorting by '" + order.getProperty() + "' is not allowed"
                );
            }}
        return ResponseEntity.ok(hotelService.listHotels(pageable,nameContains,city,description, before,after));
    }
}

