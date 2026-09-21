package com.volna.mealservice.service;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.entity.*;
import com.volna.mealservice.exception.ResourceNotFoundException;
import com.volna.mealservice.exception.BadRequestException;
import com.volna.mealservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealDeliveryService {

    private final MealPlanRepository plans;
    private final MealPlanDeliveryDayRepository deliveryDays;
    private final MealSubscriptionRepository subscriptions;
    private final MealDeliveryRecordRepository deliveries;

    @Transactional
    public DeliveryRecordResponse create(
            UUID ownerId,
            UUID planId,
            UUID subscriptionId,
            LocalDate scheduledDate) {

        MealPlan plan = ownedPlan(ownerId, planId);

        if (!deliveryDays.existsByMealPlanIdAndDayOfWeek(
                planId, scheduledDate.getDayOfWeek())) {
            throw new BadRequestException(
                "The selected date is not an active delivery day"
            );
        }

        if (subscriptionId != null) {
            MealSubscription subscription =
                subscriptions.findById(subscriptionId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + subscriptionId));

            if (!subscription.getMealPlanId().equals(planId)) {
                throw new BadRequestException(
                    "Subscription does not belong to this meal plan"
                );
            }
        }

        MealDeliveryRecord record = MealDeliveryRecord.builder()
            .id(UUID.randomUUID())
            .mealPlanId(plan.getId())
            .subscriptionId(subscriptionId)
            .scheduledDate(scheduledDate)
            .status(DeliveryStatus.SCHEDULED)
            .build();

        return response(deliveries.save(record));
    }

    @Transactional
    public DeliveryRecordResponse updateStatus(
            UUID ownerId,
            UUID deliveryId,
            DeliveryStatus status,
            String failureReason) {

        MealDeliveryRecord record =
            deliveries.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Delivery record not found: " + deliveryId));

        ownedPlan(ownerId, record.getMealPlanId());

        record.setStatus(status);
        record.setFailureReason(failureReason);

        if (status == DeliveryStatus.DELIVERED) {
            record.setDeliveredAt(OffsetDateTime.now(ZoneOffset.UTC));
        } else {
            record.setDeliveredAt(null);
        }

        return response(deliveries.save(record));
    }

    private MealPlan ownedPlan(UUID ownerId, UUID planId) {
        return plans.findByIdAndOwnerId(planId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Meal plan not found: " + planId));
    }

    private DeliveryRecordResponse response(MealDeliveryRecord r) {
        return new DeliveryRecordResponse(
            r.getId(), r.getMealPlanId(), r.getSubscriptionId(),
            r.getScheduledDate(), r.getStatus(),
            r.getDeliveredAt(), r.getFailureReason()
        );
    }
}
