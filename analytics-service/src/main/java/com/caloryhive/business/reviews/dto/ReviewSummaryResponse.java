package com.caloryhive.business.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponse {
    private Double averageRating;
    private Double ratingDelta;
    private Long totalReviews;
    private Long previousPeriodReviews;
    private Double reviewGrowthPercent;
    private Double responseRate;
    private String responseRateBadge;
    private Double responseRateTarget;
    private Double sentimentScore;
    private String sentimentLabel;
    private String period;
}
