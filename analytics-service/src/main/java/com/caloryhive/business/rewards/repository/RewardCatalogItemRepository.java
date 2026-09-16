package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.RewardCatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardCatalogItemRepository extends JpaRepository<RewardCatalogItem, UUID> {
    List<RewardCatalogItem> findByIsActiveTrueOrderByPointsCostAsc();
}
