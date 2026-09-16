package com.caloryhive.business.finance.repository;

import com.caloryhive.business.finance.entity.BusinessFinancialAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessFinancialAccountRepository extends JpaRepository<BusinessFinancialAccount, UUID> {

    Optional<BusinessFinancialAccount> findByBusinessId(UUID businessId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bfa FROM BusinessFinancialAccount bfa WHERE bfa.businessId = :businessId")
    Optional<BusinessFinancialAccount> findByBusinessIdWithLock(@Param("businessId") UUID businessId);
}
