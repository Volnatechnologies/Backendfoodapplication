package com.caloryhive.business.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {
    private Long totalOrders;
    private Double orderGrowthPercent;
    private BigDecimal totalRevenue;
    private Double revenueGrowthPercent;
    private BigDecimal averageOrderValue;
    private Double aovGrowthPercent;
    private Double customerSatisfaction;
    private Double satisfactionDelta;
    private String period;
}
