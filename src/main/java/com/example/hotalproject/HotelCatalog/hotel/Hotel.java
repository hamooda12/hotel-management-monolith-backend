package com.example.hotalproject.HotelCatalog.hotel;

import com.example.hotalproject.HotelCatalog.room.Room;
import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Hotel {
    // Fields
    private @Id
    @GeneratedValue Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private int rating;
    private String description;
    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String phoneNumber;
    private @OneToMany(mappedBy = "hotel") List<RoomType> roomTypes;

    // Constructors
    public Hotel(Long id, String name, String address, String city, String country
            , int rating, String description, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.rating = rating;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.roomTypes=new ArrayList<>();
    }

    public Hotel() {

    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

// Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
