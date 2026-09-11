package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.CateringMenuPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CateringMenuPackageRepository extends JpaRepository<CateringMenuPackage, UUID> {
    List<CateringMenuPackage> findByActiveTrueOrderByNameAsc();

    List<CateringMenuPackage> findByActiveTrueOrderByPricePerGuestAsc();

    Optional<CateringMenuPackage> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
