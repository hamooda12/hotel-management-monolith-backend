package com.example.hotalproject.HotelCatalog.roomType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long>, JpaSpecificationExecutor<RoomType> {

    List<RoomType> findByHotelId(Long hotelId);
}

