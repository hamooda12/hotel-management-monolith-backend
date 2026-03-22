package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.BusinessValidationException;
import com.example.hotalproject.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types", description = "Room type management")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;
    @GetMapping
    @Operation(summary = "Browse RoomsType with filters and pagination")
    public ResponseEntity<PagedResponse<RoomTypeResponseDto>> browseroomtype(
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            Pageable pageable,
            @RequestParam(required = false) String aminities,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) Integer mincapacity,
            @RequestParam(required = false) Integer maxcapacity,
            @RequestParam(required = false) Integer mintotalroom,
            @RequestParam(required = false) Integer maxtotalroom,
            @RequestParam(required = false) Integer minprice,
            @RequestParam(required = false) Integer maxprice,
      HttpServletRequest request
    ) {

        Set<String> ALLOWED_PARAMS = Set.of(
                "page", "size", "sort",
                "aminities", "nameContains",
                "mincapacity", "maxcapacity",
                "mintotalroom", "maxtotalroom",
                "minprice", "maxprice"
        );
        for (String paramName : request.getParameterMap().keySet()) {
            if (!ALLOWED_PARAMS.contains(paramName)) {
                throw new BusinessValidationException("Query parameter '" + paramName + "' is not allowed");
            }
        }
        LinkedList<String> ALLOWED = new LinkedList<>();
        ALLOWED.addAll(Arrays.asList("aminities", "basePrice", "totalRooms","name", "capacity","hotel","id"));

        for (var order : pageable.getSort()) {
            if (!ALLOWED.contains(order.getProperty())) {
                throw new BusinessValidationException(
                        "Sorting by '" + order.getProperty() + "' is not allowed"
                );
            }}


        return ResponseEntity.ok(roomTypeService.listRoomType(pageable,nameContains,aminities,mincapacity,maxcapacity,mintotalroom,maxtotalroom,minprice,maxprice));
    }

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
        RoomType room=roomTypeService.getRoomType(id).orElseThrow(()-> new RoomTypeNotFoundException("There is no roomtype"));
        return ResponseEntity.ok(RoomTypeMapper.toResponse(room));
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

