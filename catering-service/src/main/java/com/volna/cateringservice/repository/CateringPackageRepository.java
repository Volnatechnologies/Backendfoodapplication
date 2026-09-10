package com.volna.cateringservice.repository;

import com.volna.cateringservice.entity.CateringPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CateringPackageRepository extends JpaRepository<CateringPackage, Long> {
    List<CateringPackage> findByOwnerIdOrderByNameAsc(String ownerId);
    List<CateringPackage> findByOwnerIdAndActiveTrueOrderByNameAsc(String ownerId);
    Optional<CateringPackage> findByIdAndOwnerId(Long id, String ownerId);
}
