package com.volna.mealservice.repository;
import com.volna.mealservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    List<MealPlan> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
    List<MealPlan> findByOwnerIdAndStatusAndActiveTrueOrderByCreatedAtDesc(UUID ownerId, MealPlanStatus status);
    Optional<MealPlan> findByIdAndOwnerId(UUID id, UUID ownerId);
}
