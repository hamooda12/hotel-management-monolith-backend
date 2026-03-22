package com.example.hotalproject.HotelCatalog.roomType;


import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RoomTypeSpecifications {

    public static Specification<RoomType> nameContains(String nameContains) {
        return (root, query, cb) ->
                nameContains == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%");
    }
    public static Specification<RoomType> amenitiesContains(String amenities) {
        return (root, query, cb) ->
                amenities == null ? null :
                        cb.like(cb.lower(root.get("amenities")), "%" + amenities.toLowerCase() + "%");
    }
//
    public static Specification<RoomType> capacityBetween(Integer mincapacity, Integer maxcapacity) {
        return (root, query, cb) ->
        {
        if (mincapacity == null && maxcapacity == null) return null;
        if (mincapacity != null && maxcapacity != null) return cb.between(root.get("capacity"), mincapacity, maxcapacity);
        if (mincapacity != null && maxcapacity==null) return cb.greaterThanOrEqualTo(root.get("capacity"), mincapacity);
        return cb.lessThanOrEqualTo(root.get("capacity"), maxcapacity);

        };
    }
//    totalrooms

    public static Specification<RoomType> totalroomsBetween(Integer mintotalrooms, Integer maxtotalrooms) {
        return (root, query, cb) ->
        {
            if (mintotalrooms == null && maxtotalrooms == null) return null;
            if (mintotalrooms != null && maxtotalrooms != null) return cb.between(root.get("totalRooms"), mintotalrooms, maxtotalrooms);
            if (mintotalrooms != null) return cb.greaterThanOrEqualTo(root.get("totalRooms"), mintotalrooms);
            return cb.lessThanOrEqualTo(root.get("totalRooms"), maxtotalrooms);
        };
    }
    public static Specification<RoomType> SalaryBetween(BigDecimal minsalary, BigDecimal maxsalary) {
        return (root, query, cb) -> {
            if (minsalary == null && maxsalary == null) return null;
            if (minsalary != null && maxsalary != null) return cb.between(root.get("basePrice"), minsalary, maxsalary);
            if (minsalary != null) return cb.greaterThanOrEqualTo(root.get("basePrice"), minsalary);
            return cb.lessThanOrEqualTo(root.get("basePrice"), maxsalary);
        };
    }

}
