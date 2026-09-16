package com.caloryhive.business.reviews.controller;

import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.reviews.dto.*;
import com.caloryhive.business.reviews.service.ReviewService;
import com.caloryhive.business.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/business/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews & Feedback", description = "Endpoints for guest reviews, rating distribution, sentiment analytics, and manager responses")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get reviews overview summary", description = "Calculates average rating, total reviews count, response rate, and sentiment score.")
    public ApiResponse<ReviewSummaryResponse> getSummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "30D") String period
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(reviewService.getSummary(businessId, period), "Review summary fetched successfully");
    }

    @GetMapping("/distribution")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get rating distribution", description = "Returns 1 to 5 star rating counts and percentages.")
    public ApiResponse<RatingDistributionResponse> getDistribution(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(reviewService.getDistribution(businessId), "Rating distribution fetched successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get reviews with filtering and pagination", description = "Filters reviews by tab (ALL, WITH_PHOTOS, NEGATIVE_ONLY, UNANSWERED), star rating, or search text.")
    public ApiResponse<Page<ReviewResponseDto>> getReviews(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(reviewService.getReviews(businessId, filter, rating, search, pageable), "Reviews fetched successfully");
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get a single review by ID")
    public ApiResponse<ReviewResponseDto> getReviewById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(reviewService.getReviewById(businessId, reviewId), "Review fetched successfully");
    }

    @PostMapping("/{reviewId}/response")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Reply to a review", description = "Persists sanitized response, marks review as answered, and records audit trail.")
    public ApiResponse<ReviewReplyResponse> respondToReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewReplyRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");
        String name = principal != null && principal.getEmail() != null ? principal.getEmail() : "Restaurant Manager";

        ReviewReplyResponse reply = reviewService.respondToReview(
                businessId, userId, name, reviewId, request,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(reply, "Response added successfully");
    }

    @PutMapping("/{reviewId}/response")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Update review response")
    public ApiResponse<ReviewReplyResponse> updateResponse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewReplyRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        ReviewReplyResponse reply = reviewService.updateResponse(
                businessId, userId, reviewId, request,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(reply, "Response updated successfully");
    }

    @DeleteMapping("/{reviewId}/response")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Delete review response")
    public ApiResponse<Void> deleteResponse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID reviewId,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        reviewService.deleteResponse(
                businessId, userId, reviewId,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(null, "Response deleted successfully");
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
