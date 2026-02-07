package com.example.hotalproject.HotelCatalog.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class HotelResponseDto {
    private final Long id;
    @NotBlank(message = "Hotel name is mandatory")
    private String name;
    @NotBlank(message = "Hotel address is mandatory")
    private String address;
    @Min(1)
    @Max(5)
    private int rating;
    private String city;
    private String country;
    private String description;
    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String phoneNumber;


    public HotelResponseDto(Long id, String name, String address, int rating, String city, String country, String description, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.city = city;
        this.country = country;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRating() {
        return rating;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
