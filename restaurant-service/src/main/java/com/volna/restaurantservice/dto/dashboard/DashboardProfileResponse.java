package com.volna.restaurantservice.dto.dashboard;

public record DashboardProfileResponse(
        int completionPercentage,
        boolean profileComplete,
        String nextAction
) {
}