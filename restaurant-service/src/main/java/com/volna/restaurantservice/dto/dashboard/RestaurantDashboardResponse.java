package com.volna.restaurantservice.dto.dashboard;

import java.time.OffsetDateTime;

public record RestaurantDashboardResponse(
        DashboardRestaurantResponse restaurant,
        DashboardMetricsResponse metrics,
        DashboardDocumentStatsResponse documents,
        DashboardProfileResponse profile,
        OffsetDateTime generatedAt
) {
}