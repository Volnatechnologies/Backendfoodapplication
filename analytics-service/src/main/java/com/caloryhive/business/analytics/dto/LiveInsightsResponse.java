package com.caloryhive.business.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveInsightsResponse {
    private String peakBusyHours;
    private String peakDays;
    private Integer avgFulfillmentTimeMinutes;
    private String fulfillmentContext;
}
