package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.BusinessRewardAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRewardAccountRepository extends JpaRepository<BusinessRewardAccount, UUID> {

    Optional<BusinessRewardAccount> findByBusinessId(UUID businessId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bra FROM BusinessRewardAccount bra WHERE bra.businessId = :businessId")
    Optional<BusinessRewardAccount> findByBusinessIdWithLock(@Param("businessId") UUID businessId);
}
