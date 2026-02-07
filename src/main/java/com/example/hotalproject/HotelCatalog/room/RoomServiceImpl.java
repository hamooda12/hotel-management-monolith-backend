package com.example.hotalproject.HotelCatalog.room;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class RoomServiceImpl implements RoomService {
    private final RoomRepository repository;
    @Override
    public Room createRoom(Room room) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    @Override
    public Room getRoomById(Long id) {
        return null;
    }

    @Override
    public Room updateRoom(Long id, Room room) {
        return null;
    }

    @Override
    public void deleteRoom(Long id) {

    }

    @Override
    public Room replaceRoom(Long id, Room room) {
        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        return List.of();
    }
}
