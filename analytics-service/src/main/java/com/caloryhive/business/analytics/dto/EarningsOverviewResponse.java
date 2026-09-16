package com.caloryhive.business.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarningsOverviewResponse {

    private String period;
    private BigDecimal totalEarnings;
    private Long totalOrders;
    private List<DailyEarningPoint> points;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyEarningPoint {
        private String date;
        private String dayOfWeek;
        private BigDecimal earnings;
        private Long ordersCount;
    }
}
