package com.caloryhive.business.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionResponse {

    private Long totalReviews;
    private List<StarRatingCount> distribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarRatingCount {
        private int stars;
        private long count;
        private double percentage;
    }
}
