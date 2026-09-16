package com.caloryhive.business.finance.repository;

import com.caloryhive.business.finance.entity.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {
    Page<Withdrawal> findByBusinessIdOrderByCreatedAtDesc(UUID businessId, Pageable pageable);
    Optional<Withdrawal> findByIdAndBusinessId(UUID id, UUID businessId);
    Optional<Withdrawal> findByIdempotencyKey(String idempotencyKey);
}
