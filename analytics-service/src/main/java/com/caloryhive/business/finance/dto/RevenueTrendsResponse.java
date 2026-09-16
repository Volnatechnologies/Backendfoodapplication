package com.caloryhive.business.finance.dto;

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
public class RevenueTrendsResponse {

    private String filter;
    private BigDecimal totalRevenue;
    private List<TrendPoint> points;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String label;
        private BigDecimal revenue;
        private Long ordersCount;
    }
}
