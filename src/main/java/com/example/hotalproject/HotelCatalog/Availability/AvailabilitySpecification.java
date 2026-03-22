package com.example.hotalproject.HotelCatalog.Availability;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvailabilitySpecification {

	public static Specification<Availability> filterAvailabilities(
			Long hotelId,
			Long roomTypeId,
			LocalDate checkinDate,
			LocalDate checkoutDate
	) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (hotelId != null) {
				predicates.add(cb.equal(root.get("hotel").get("id"), hotelId));
			}

			if (roomTypeId != null) {
				predicates.add(cb.equal(root.get("roomType").get("id"), roomTypeId));
			}

			if (checkinDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("date"), checkinDate));
			}

			if (checkoutDate != null) {
				predicates.add(cb.lessThan(root.get("date"), checkoutDate));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}