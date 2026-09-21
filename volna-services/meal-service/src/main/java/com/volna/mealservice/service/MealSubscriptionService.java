package com.volna.mealservice.service;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.entity.*;
import com.volna.mealservice.exception.*;
import com.volna.mealservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealSubscriptionService {
    private final MealPlanRepository plans;
    private final MealSubscriptionRepository subscriptions;

    @Transactional
    public MealSubscriptionResponse create(UUID ownerId, UUID planId, MealSubscriptionCreateRequest r) {
        MealPlan p = owned(ownerId, planId);
        if (!p.isActive() || p.getStatus() != MealPlanStatus.ACTIVE)
            throw new BadRequestException("Subscriptions can only be created for active meal plans");
        MealSubscription s = MealSubscription.builder()
            .id(UUID.randomUUID()).mealPlanId(planId).customerId(r.customerId())
            .customerName(r.customerName().trim()).startedAt(r.startedAt()==null?LocalDate.now():r.startedAt())
            .status(MealSubscriptionStatus.ACTIVE).build();
        return response(subscriptions.save(s));
    }

    @Transactional(readOnly=true)
    public List<MealSubscriptionResponse> list(UUID ownerId, UUID planId) {
        owned(ownerId, planId);
        return subscriptions.findByMealPlanIdAndStatus(planId, MealSubscriptionStatus.ACTIVE)
            .stream().map(this::response).toList();
    }

    @Transactional
    public MealSubscriptionResponse cancel(UUID ownerId, UUID planId, UUID subscriptionId, MealSubscriptionCancelRequest r) {
        owned(ownerId, planId);
        MealSubscription s = subscriptions.findById(subscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));
        if (!s.getMealPlanId().equals(planId)) throw new ResourceNotFoundException("Subscription does not belong to this plan");
        if (s.getStatus() == MealSubscriptionStatus.CANCELLED) throw new BadRequestException("Subscription is already cancelled");
        s.setStatus(MealSubscriptionStatus.CANCELLED); s.setCancelledAt(LocalDate.now());
        if (r != null) s.setCancellationReason(r.reason());
        return response(subscriptions.save(s));
    }

    private MealPlan owned(UUID ownerId, UUID planId) {
        return plans.findByIdAndOwnerId(planId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found: " + planId));
    }

    private MealSubscriptionResponse response(MealSubscription s) {
        return new MealSubscriptionResponse(s.getId(),s.getMealPlanId(),s.getCustomerId(),s.getCustomerName(),
            s.getStatus(),s.getStartedAt(),s.getCancelledAt(),s.getCancellationReason());
    }
}
