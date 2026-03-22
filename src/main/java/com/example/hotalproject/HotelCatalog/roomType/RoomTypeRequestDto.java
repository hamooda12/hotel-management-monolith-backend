package com.example.hotalproject.HotelCatalog.roomType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeRequestDto {



    @NotBlank(message = "Room type name is required")
    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    private String amenities;

    @Min(value = 1, message = "Total rooms must be at least 1")
    private int totalRooms;
}

