package com.example.hotalproject.HotelCatalog.hotel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("""
            SELECT DISTINCT h FROM Hotel h
            LEFT JOIN RoomType rt ON rt.hotel = h
            WHERE (:city IS NULL OR LOWER(h.city) = LOWER(:city))
              AND (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:minCapacity IS NULL OR rt.capacity >= :minCapacity)
              AND (:minPrice IS NULL OR rt.basePrice >= :minPrice)
              AND (:maxPrice IS NULL OR rt.basePrice <= :maxPrice)
            """)
    Page<Hotel> findWithFilters(@Param("city") String city,
                                @Param("name") String name,
                                @Param("minCapacity") Integer minCapacity,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                Pageable pageable);
}

