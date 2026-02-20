package com.example.hotalproject.HotelCatalog.hotel;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class HotelSpecifications {

    public static Specification<Hotel> CraciteriaContains(String nameContains) {
        return (root, query, cb) ->
                nameContains == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%");
    }

    public static Specification<Hotel> CriatedDateBetween(LocalDate after, LocalDate before) {
        return (root, query, cb) -> {
            if (after == null && before == null) return null;
            if (after != null && before != null) return cb.between(root.get("hiredDate"), after, before);
            if (after != null) return cb.greaterThanOrEqualTo(root.get("hiredDate"), after);
            return cb.lessThanOrEqualTo(root.get("hiredDate"), before);
        };
    }

}
