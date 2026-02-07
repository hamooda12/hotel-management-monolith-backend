package com.example.hotalproject.HotelCatalog.roomType;

import com.example.hotalproject.HotelCatalog.hotel.Hotel;
import com.example.hotalproject.HotelCatalog.room.Room;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
@Entity
public class RoomType {

    @Id @GeneratedValue
    private long id;
    private String name;
    //
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    //
    private int capacityAdults;
    private int capacityChildren;
    private int maxGuests;
    private Double basePrice;
    private int totalInventory;
    private String amenities; // خليها String هسا (مثلاً "WiFi,AC,Breakfast")
    //
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    public RoomType(long id, List<Room> rooms, String amenities, int totalInventory, int maxGuests, Double basePrice, int capacityChildren, int capacityAdults, Hotel hotel, String name) {
        this.id = id;
        this.rooms = rooms;
        this.amenities = amenities;
        this.totalInventory = totalInventory;
        this.maxGuests = maxGuests;
        this.basePrice = basePrice;
        this.capacityChildren = capacityChildren;
        this.capacityAdults = capacityAdults;
        this.hotel = hotel;
        this.name = name;
    }

    public RoomType() {

    }
//

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacityAdults() {
        return capacityAdults;
    }

    public void setCapacityAdults(int capacityAdults) {
        this.capacityAdults = capacityAdults;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public int getCapacityChildren() {
        return capacityChildren;
    }

    public void setCapacityChildren(int capacityChildren) {
        this.capacityChildren = capacityChildren;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public int getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(int totalInventory) {
        this.totalInventory = totalInventory;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

}
