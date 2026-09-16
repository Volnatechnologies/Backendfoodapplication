package com.caloryhive.business.finance.repository;

import com.caloryhive.business.finance.entity.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {
    Page<FinancialTransaction> findByBusinessIdOrderByCreatedAtDesc(UUID businessId, Pageable pageable);
    List<FinancialTransaction> findByBusinessIdOrderByCreatedAtDesc(UUID businessId);

    @Query("SELECT ft FROM FinancialTransaction ft WHERE ft.businessId = :businessId AND ft.createdAt >= :startDate AND ft.createdAt <= :endDate ORDER BY ft.createdAt ASC")
    List<FinancialTransaction> findByBusinessIdAndPeriod(
            @Param("businessId") UUID businessId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}
