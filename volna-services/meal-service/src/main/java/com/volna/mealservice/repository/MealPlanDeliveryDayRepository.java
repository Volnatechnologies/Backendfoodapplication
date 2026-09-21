package com.volna.mealservice.repository;
import com.volna.mealservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.*;

public interface MealPlanDeliveryDayRepository extends JpaRepository<MealPlanDeliveryDay, MealPlanDeliveryDayId> {
    List<MealPlanDeliveryDay> findByMealPlanId(UUID mealPlanId);
    void deleteByMealPlanId(UUID mealPlanId);
    boolean existsByMealPlanIdAndDayOfWeek(UUID mealPlanId, DayOfWeek dayOfWeek);
}
