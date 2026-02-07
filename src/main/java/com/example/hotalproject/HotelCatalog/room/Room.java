package com.example.hotalproject.HotelCatalog.room;

import com.example.hotalproject.HotelCatalog.roomType.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
public class Room {
    // Fields
    private @Id @GeneratedValue Long id;
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    // Constructors
    public Room(Long id, RoomType roomType) {
        this.id = id;
        this.roomType = roomType;
    }

    public Room() {

    }

// Getters
    public Long getId() {
        return id;
    }

    public RoomType getRoomType() {
        return roomType;
    }

// Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }


}
