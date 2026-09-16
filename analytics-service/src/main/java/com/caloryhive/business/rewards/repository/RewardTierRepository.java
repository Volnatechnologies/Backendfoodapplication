package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.RewardTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardTierRepository extends JpaRepository<RewardTier, UUID> {
    Optional<RewardTier> findByTierName(String tierName);
}
