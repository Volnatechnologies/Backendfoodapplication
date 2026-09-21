package com.volna.restaurantservice.dto.dashboard;
import java.math.BigDecimal;

public record DashboardMetricsResponse(
        long totalOrders,
        long todayOrders,
        long pendingOrders,
        long activeOrders,
        BigDecimal totalRevenue,
        BigDecimal todayRevenue,
        boolean orderMetricsAvailable,
        String orderMetricsSource
) {
}