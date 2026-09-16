package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.RewardRedemption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, UUID> {
    Page<RewardRedemption> findByBusinessIdOrderByCreatedAtDesc(UUID businessId, Pageable pageable);
}
