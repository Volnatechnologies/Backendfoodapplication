package com.volna.mealservice.repository;
import com.volna.mealservice.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface MealSubscriptionRepository extends JpaRepository<MealSubscription, UUID> {
    long countByMealPlanIdAndStatus(UUID mealPlanId, MealSubscriptionStatus status);
    List<MealSubscription> findByMealPlanIdAndStatus(UUID mealPlanId, MealSubscriptionStatus status);

    @Query("SELECT s FROM MealSubscription s JOIN MealPlan p ON p.id=s.mealPlanId " +
           "WHERE p.ownerId=:ownerId AND s.status=:status ORDER BY s.startedAt DESC")
    List<MealSubscription> findByOwnerIdAndStatus(
        @Param("ownerId") UUID ownerId, @Param("status") MealSubscriptionStatus status);

    @Query("SELECT s FROM MealSubscription s JOIN MealPlan p ON p.id=s.mealPlanId " +
           "WHERE p.ownerId=:ownerId AND s.status='CANCELLED' AND s.cancelledAt>=:fromDate " +
           "ORDER BY s.cancelledAt DESC")
    List<MealSubscription> findRecentCancellations(
        @Param("ownerId") UUID ownerId, @Param("fromDate") LocalDate fromDate);
}
