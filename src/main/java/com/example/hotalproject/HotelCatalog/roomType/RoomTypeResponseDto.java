package com.example.hotalproject.HotelCatalog.roomType;

import jakarta.validation.constraints.Min;

public class RoomTypeResponseDto {
    private int capacityAdults;
    private int capacityChildren;
    private int maxGuests;
    private Double basePrice;
    private int totalInventory;
    private String amenities; // خليها String هسا (مثلاً "WiFi,AC,Breakfast")
    private String hotelName; // اسم الفندق اللي ينتمي له نوع الغرفة


    public RoomTypeResponseDto(int capacityAdults, int capacityChildren,
                               int maxGuests, Double basePrice, int totalInventory,
                               String amenities, String hotelName) {
        this.capacityAdults = capacityAdults;
        this.capacityChildren = capacityChildren;
        this.maxGuests = maxGuests;
        this.basePrice = basePrice;
        this.totalInventory = totalInventory;
        this.amenities = amenities;
        this.hotelName = hotelName;
    }


    public int getCapacityAdults() {
        return capacityAdults;
    }

    public int getCapacityChildren() {
        return capacityChildren;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public int getTotalInventory() {
        return totalInventory;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getHotelName() {
        return hotelName;
    }
}
