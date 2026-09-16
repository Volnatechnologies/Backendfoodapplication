package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.RewardPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardPerkRepository extends JpaRepository<RewardPerk, UUID> {
    List<RewardPerk> findByTierIdOrderBySortOrderAsc(UUID tierId);
}
