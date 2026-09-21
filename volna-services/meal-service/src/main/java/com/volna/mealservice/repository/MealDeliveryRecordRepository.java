package com.volna.mealservice.repository;
import com.volna.mealservice.entity.MealDeliveryRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface MealDeliveryRecordRepository extends JpaRepository<MealDeliveryRecord, UUID> {
    @Query("SELECT COUNT(d) FROM MealDeliveryRecord d JOIN MealPlan p ON p.id=d.mealPlanId " +
           "WHERE p.ownerId=:ownerId AND d.status='DELIVERED' AND d.scheduledDate BETWEEN :fromDate AND :toDate")
    long countDelivered(@Param("ownerId") UUID ownerId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(d) FROM MealDeliveryRecord d JOIN MealPlan p ON p.id=d.mealPlanId " +
           "WHERE p.ownerId=:ownerId AND d.status IN ('DELIVERED','FAILED') AND d.scheduledDate BETWEEN :fromDate AND :toDate")
    long countAttempted(@Param("ownerId") UUID ownerId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
