package com.volna.mealservice.service;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.entity.*;
import com.volna.mealservice.exception.*;
import com.volna.mealservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanService {
    private final MealPlanRepository plans;
    private final MealPlanDeliveryDayRepository days;
    private final MealPlanItemRepository items;
    private final MealSubscriptionRepository subscriptions;

    @Transactional
    public MealPlanResponse create(UUID ownerId, CreateMealPlanRequest r) {
        MealPlan p = MealPlan.builder()
            .id(UUID.randomUUID()).ownerId(ownerId).name(r.name().trim())
            .description(r.description()).pricingModel(r.pricingModel()).basePrice(r.basePrice())
            .currency("USD").mealsPerDay(r.mealsPerDay()).firstDeliveryDate(r.firstDeliveryDate())
            .lifecycle(r.lifecycle()).status(MealPlanStatus.DRAFT).active(true).build();
        plans.save(p);
        replaceDays(p.getId(), r.deliveryDays());
        if (r.menuItems() != null) r.menuItems().forEach(x -> addItemInternal(p.getId(), x));
        return response(p);
    }

    @Transactional(readOnly=true)
    public List<MealPlanResponse> list(UUID ownerId) {
        return plans.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(this::response).toList();
    }

    @Transactional(readOnly=true)
    public MealPlanResponse get(UUID ownerId, UUID id) {
        return response(findOwned(ownerId, id));
    }

    @Transactional
    public MealPlanResponse update(UUID ownerId, UUID id, UpdateMealPlanRequest r) {
        MealPlan p = findOwned(ownerId, id);
        p.setName(r.name().trim()); p.setDescription(r.description());
        p.setPricingModel(r.pricingModel()); p.setBasePrice(r.basePrice());
        p.setMealsPerDay(r.mealsPerDay()); p.setFirstDeliveryDate(r.firstDeliveryDate());
        p.setLifecycle(r.lifecycle());
        replaceDays(id, r.deliveryDays());
        return response(plans.save(p));
    }

    @Transactional
    public MealPlanResponse publish(UUID ownerId, UUID id) {
        MealPlan p = findOwned(ownerId, id);
        if (items.findByMealPlanIdAndActiveTrueOrderByCreatedAtAsc(id).isEmpty())
            throw new BadRequestException("Add at least one menu item before publishing");
        if (days.findByMealPlanId(id).isEmpty())
            throw new BadRequestException("Select at least one delivery day before publishing");
        p.setStatus(MealPlanStatus.ACTIVE); p.setActive(true);
        return response(plans.save(p));
    }

    @Transactional
    public MealPlanResponse pause(UUID ownerId, UUID id) {
        MealPlan p = findOwned(ownerId, id);
        if (p.getStatus() != MealPlanStatus.ACTIVE) throw new BadRequestException("Only an active plan can be paused");
        p.setStatus(MealPlanStatus.PAUSED);
        return response(plans.save(p));
    }

    @Transactional
    public MealPlanResponse resume(UUID ownerId, UUID id) {
        MealPlan p = findOwned(ownerId, id);
        if (p.getStatus() != MealPlanStatus.PAUSED) throw new BadRequestException("Only a paused plan can be resumed");
        p.setStatus(MealPlanStatus.ACTIVE); p.setActive(true);
        return response(plans.save(p));
    }

    @Transactional
    public void archive(UUID ownerId, UUID id) {
        MealPlan p = findOwned(ownerId, id);
        p.setStatus(MealPlanStatus.ARCHIVED); p.setActive(false); plans.save(p);
    }

    @Transactional
    public MealPlanItemResponse addItem(UUID ownerId, UUID planId, MealPlanItemRequest r) {
        findOwned(ownerId, planId);
        return itemResponse(addItemInternal(planId, r));
    }

    @Transactional
    public void removeItem(UUID ownerId, UUID planId, UUID itemId) {
        findOwned(ownerId, planId);
        MealPlanItem item = items.findByIdAndMealPlanIdAndActiveTrue(itemId, planId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemId));
        item.setActive(false); items.save(item);
    }

    private MealPlanItem addItemInternal(UUID planId, MealPlanItemRequest r) {
        return items.save(MealPlanItem.builder()
            .id(UUID.randomUUID()).mealPlanId(planId).name(r.name().trim())
            .description(r.description()).imageUrl(r.imageUrl())
            .dietaryType(r.dietaryType() == null ? MealDietaryType.GENERAL : r.dietaryType())
            .active(true).build());
    }

    private void replaceDays(UUID planId, List<DayOfWeek> deliveryDays) {
        days.deleteByMealPlanId(planId);
        new LinkedHashSet<>(deliveryDays).forEach(d -> days.save(new MealPlanDeliveryDay(planId, d)));
    }

    private MealPlan findOwned(UUID ownerId, UUID id) {
        return plans.findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found: " + id));
    }

    private MealPlanResponse response(MealPlan p) {
        List<DayOfWeek> ds = days.findByMealPlanId(p.getId()).stream()
            .map(MealPlanDeliveryDay::getDayOfWeek).sorted().toList();
        List<MealPlanItemResponse> is = items.findByMealPlanIdAndActiveTrueOrderByCreatedAtAsc(p.getId())
            .stream().map(this::itemResponse).toList();
        long count = subscriptions.countByMealPlanIdAndStatus(p.getId(), MealSubscriptionStatus.ACTIVE);
        return new MealPlanResponse(p.getId(),p.getName(),p.getDescription(),p.getPricingModel(),
            p.getBasePrice(),p.getCurrency(),p.getMealsPerDay(),p.getFirstDeliveryDate(),
            p.getLifecycle(),p.getStatus(),count,ds,is);
    }

    private MealPlanItemResponse itemResponse(MealPlanItem i) {
        return new MealPlanItemResponse(i.getId(),i.getName(),i.getDescription(),i.getImageUrl(),i.getDietaryType(),i.isActive());
    }
}
