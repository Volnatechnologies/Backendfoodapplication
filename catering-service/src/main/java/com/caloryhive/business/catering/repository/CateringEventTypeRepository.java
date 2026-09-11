package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.CateringEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CateringEventTypeRepository extends JpaRepository<CateringEventType, UUID> {
    List<CateringEventType> findByActiveTrueOrderByNameAsc();

    Optional<CateringEventType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
