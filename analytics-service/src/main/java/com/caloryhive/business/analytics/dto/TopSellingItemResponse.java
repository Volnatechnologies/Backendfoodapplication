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
public class TopSellingItemResponse {
    private String name;
    private String category;
    private Long ordersCount;
    private BigDecimal revenue;
    private Double progressPercentage;
    private String imageUrl;
}
