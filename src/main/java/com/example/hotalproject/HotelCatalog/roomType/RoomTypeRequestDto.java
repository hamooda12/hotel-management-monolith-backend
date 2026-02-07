package com.example.hotalproject.HotelCatalog.roomType;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

public class RoomTypeRequestDto {
    @NotBlank
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
