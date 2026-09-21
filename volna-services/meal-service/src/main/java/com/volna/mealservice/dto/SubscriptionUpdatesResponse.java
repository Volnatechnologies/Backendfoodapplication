package com.volna.mealservice.dto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public record SubscriptionUpdatesResponse(
    List<UpcomingRotation> upcomingRotations,
    List<Cancellation> recentCancellations,
    long cancellationsThisWeek
) {
    public record UpcomingRotation(UUID planId, String planName, LocalDate startsOn, String note) {}
    public record Cancellation(UUID subscriptionId, String customerName, String planName,
                               String reason, LocalDate cancelledAt) {}
}
