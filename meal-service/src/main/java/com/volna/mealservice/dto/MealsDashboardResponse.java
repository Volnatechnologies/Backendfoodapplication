package com.volna.mealservice.dto;
import java.math.BigDecimal;
import java.util.List;
public record MealsDashboardResponse(
    long totalSubscribers,
    long activeMealPlans,
    BigDecimal recurringRevenueMonthly,
    BigDecimal fulfillmentRate,
    List<MealPlanResponse> activePlans,
    List<MenuRotationDayResponse> rotation,
    SubscriptionUpdatesResponse subscriptionUpdates
) {}
