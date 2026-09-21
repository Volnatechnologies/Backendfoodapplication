package com.volna.mealservice.repository;
import com.volna.mealservice.entity.MealPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MealPlanItemRepository extends JpaRepository<MealPlanItem, UUID> {
    List<MealPlanItem> findByMealPlanIdAndActiveTrueOrderByCreatedAtAsc(UUID mealPlanId);
    Optional<MealPlanItem> findByIdAndMealPlanIdAndActiveTrue(UUID id, UUID mealPlanId);
}
