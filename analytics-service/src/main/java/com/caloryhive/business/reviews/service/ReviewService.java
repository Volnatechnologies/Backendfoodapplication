package com.caloryhive.business.reviews.service;

import com.caloryhive.business.reviews.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {

    ReviewSummaryResponse getSummary(UUID businessId, String period);

    RatingDistributionResponse getDistribution(UUID businessId);

    Page<ReviewResponseDto> getReviews(UUID businessId, String filter, Integer rating, String search, Pageable pageable);

    ReviewResponseDto getReviewById(UUID businessId, UUID reviewId);

    ReviewReplyResponse respondToReview(UUID businessId, UUID userId, String responderName, UUID reviewId, ReviewReplyRequest request, String ipAddress, String userAgent);

    ReviewReplyResponse updateResponse(UUID businessId, UUID userId, UUID reviewId, ReviewReplyRequest request, String ipAddress, String userAgent);

    void deleteResponse(UUID businessId, UUID userId, UUID reviewId, String ipAddress, String userAgent);
}
