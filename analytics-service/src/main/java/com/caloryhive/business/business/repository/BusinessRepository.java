package com.caloryhive.business.business.repository;

import com.caloryhive.business.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {
    Optional<Business> findByOwnerId(UUID ownerId);
    Optional<Business> findByEmail(String email);
}
