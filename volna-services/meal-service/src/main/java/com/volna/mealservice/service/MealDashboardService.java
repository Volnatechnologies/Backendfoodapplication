package com.volna.mealservice.service;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.entity.*;
import com.volna.mealservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealDashboardService {
    private final MealPlanRepository plans;
    private final MealPlanDeliveryDayRepository days;
    private final MealPlanItemRepository items;
    private final MealSubscriptionRepository subscriptions;
    private final MealDeliveryRecordRepository deliveries;

    @Transactional(readOnly=true)
    public MealsDashboardResponse dashboard(UUID ownerId) {
        List<MealPlan> active = plans.findByOwnerIdAndStatusAndActiveTrueOrderByCreatedAtDesc(ownerId, MealPlanStatus.ACTIVE);
        List<MealSubscription> activeSubs = subscriptions.findByOwnerIdAndStatus(ownerId, MealSubscriptionStatus.ACTIVE);
        BigDecimal revenue = activeSubs.stream()
            .map(s -> monthlyPrice(plans.findById(s.getMealPlanId()).orElse(null)))
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        LocalDate end = LocalDate.now(), start = end.minusDays(30);
        long delivered = deliveries.countDelivered(ownerId,start,end);
        long attempted = deliveries.countAttempted(ownerId,start,end);
        BigDecimal fulfillment = attempted == 0 ? BigDecimal.ZERO.setScale(1) :
            BigDecimal.valueOf(delivered * 100.0 / attempted).setScale(1, RoundingMode.HALF_UP);

        return new MealsDashboardResponse(
            activeSubs.size(), active.size(), revenue, fulfillment,
            active.stream().map(this::planResponse).toList(),
            rotation(ownerId,7), updates(ownerId)
        );
    }

    @Transactional(readOnly=true)
    public List<MenuRotationDayResponse> rotation(UUID ownerId, int requestedDays) {
        int count = Math.max(1, Math.min(requestedDays,14));
        List<MealPlan> active = plans.findByOwnerIdAndStatusAndActiveTrueOrderByCreatedAtDesc(ownerId, MealPlanStatus.ACTIVE);
        List<MenuRotationDayResponse> result = new ArrayList<>();

        for (int i=0;i<count;i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            List<MealPlanRotationResponse> rp = active.stream()
                .filter(p -> days.existsByMealPlanIdAndDayOfWeek(p.getId(),date.getDayOfWeek()))
                .map(p -> new MealPlanRotationResponse(p.getId(),p.getName(),
                    items.findByMealPlanIdAndActiveTrueOrderByCreatedAtAsc(p.getId())
                        .stream().map(this::itemResponse).toList()))
                .toList();
            result.add(new MenuRotationDayResponse(date,date.getDayOfWeek().name(),!rp.isEmpty(),rp));
        }
        return result;
    }

    @Transactional(readOnly=true)
    public SubscriptionUpdatesResponse updates(UUID ownerId) {
        LocalDate today=LocalDate.now();
        List<MealPlan> active=plans.findByOwnerIdAndStatusAndActiveTrueOrderByCreatedAtDesc(ownerId,MealPlanStatus.ACTIVE);

        List<SubscriptionUpdatesResponse.UpcomingRotation> upcoming = active.stream()
            .filter(p -> p.getFirstDeliveryDate()!=null && !p.getFirstDeliveryDate().isBefore(today))
            .sorted(Comparator.comparing(MealPlan::getFirstDeliveryDate)).limit(5)
            .map(p -> new SubscriptionUpdatesResponse.UpcomingRotation(
                p.getId(),p.getName(),p.getFirstDeliveryDate(),"Upcoming meal-plan delivery cycle"))
            .toList();

        List<MealSubscription> cancelled=subscriptions.findRecentCancellations(ownerId,today.minusDays(7));
        List<SubscriptionUpdatesResponse.Cancellation> recent=cancelled.stream().map(s -> {
            String name=plans.findById(s.getMealPlanId()).map(MealPlan::getName).orElse("Meal Plan");
            return new SubscriptionUpdatesResponse.Cancellation(s.getId(),s.getCustomerName(),name,s.getCancellationReason(),s.getCancelledAt());
        }).toList();

        return new SubscriptionUpdatesResponse(upcoming,recent,recent.size());
    }

    private BigDecimal monthlyPrice(MealPlan p) {
        if (p==null) return null;
        return switch(p.getPricingModel()) {
            case WEEKLY -> p.getBasePrice().multiply(BigDecimal.valueOf(4.345));
            case BI_WEEKLY -> p.getBasePrice().multiply(BigDecimal.valueOf(2.1725));
            case MONTHLY -> p.getBasePrice();
        };
    }

    private MealPlanResponse planResponse(MealPlan p) {
        List<DayOfWeek> ds=days.findByMealPlanId(p.getId()).stream().map(MealPlanDeliveryDay::getDayOfWeek).sorted().toList();
        List<MealPlanItemResponse> is=items.findByMealPlanIdAndActiveTrueOrderByCreatedAtAsc(p.getId()).stream().map(this::itemResponse).toList();
        long sub=subscriptions.countByMealPlanIdAndStatus(p.getId(),MealSubscriptionStatus.ACTIVE);
        return new MealPlanResponse(p.getId(),p.getName(),p.getDescription(),p.getPricingModel(),p.getBasePrice(),p.getCurrency(),
            p.getMealsPerDay(),p.getFirstDeliveryDate(),p.getLifecycle(),p.getStatus(),sub,ds,is);
    }

    private MealPlanItemResponse itemResponse(MealPlanItem i) {
        return new MealPlanItemResponse(i.getId(),i.getName(),i.getDescription(),i.getImageUrl(),i.getDietaryType(),i.isActive());
    }
}
