package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.RewardTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, UUID> {
    Page<RewardTransaction> findByBusinessIdOrderByCreatedAtDesc(UUID businessId, Pageable pageable);
}
