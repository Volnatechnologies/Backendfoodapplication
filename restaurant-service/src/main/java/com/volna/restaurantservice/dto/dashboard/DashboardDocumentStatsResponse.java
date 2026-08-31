package com.volna.restaurantservice.dto.dashboard;

public record DashboardDocumentStatsResponse(
        long total,
        long approved,
        long pending,
        long rejected,
        boolean allApproved
) {
}