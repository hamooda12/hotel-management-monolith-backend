package com.example.hotalproject.HotelCatalog.Availability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AvailabilityRepository extends JpaRepository<Availability, Long>, JpaSpecificationExecutor<Availability> {
}