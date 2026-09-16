package com.caloryhive.business.reviews;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.reviews.dto.ReviewReplyRequest;
import com.caloryhive.business.reviews.dto.ReviewReplyResponse;
import com.caloryhive.business.reviews.dto.ReviewSummaryResponse;
import com.caloryhive.business.reviews.entity.Review;
import com.caloryhive.business.reviews.entity.ReviewResponse;
import com.caloryhive.business.reviews.repository.ReviewRepository;
import com.caloryhive.business.reviews.repository.ReviewResponseRepository;
import com.caloryhive.business.reviews.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewResponseRepository reviewResponseRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID businessId;
    private UUID reviewId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void testGetSummary() {
        when(reviewRepository.countByBusinessId(businessId)).thenReturn(1420L);
        when(reviewRepository.avgRatingByBusinessId(businessId)).thenReturn(4.82);
        when(reviewRepository.countAnsweredByBusinessId(businessId)).thenReturn(1392L);
        when(reviewRepository.avgSentimentScoreByBusinessId(businessId)).thenReturn(92.4);

        ReviewSummaryResponse summary = reviewService.getSummary(businessId, "30D");

        assertNotNull(summary);
        assertEquals(4.8, summary.getAverageRating());
        assertEquals(1420L, summary.getTotalReviews());
        assertEquals(98.0, summary.getResponseRate());
        assertEquals(92.4, summary.getSentimentScore());
    }

    @Test
    void testRespondToReviewSuccessWithSanitization() {
        Review review = Review.builder()
                .id(reviewId)
                .businessId(businessId)
                .customerName("Jane Doe")
                .comment("Lovely ambience and delicious truffle burger!")
                .rating(5)
                .answered(false)
                .build();

        when(reviewRepository.findByIdAndBusinessId(reviewId, businessId)).thenReturn(Optional.of(review));
        when(reviewResponseRepository.save(any(ReviewResponse.class))).thenAnswer(inv -> {
            ReviewResponse resp = inv.getArgument(0);
            resp.setId(UUID.randomUUID());
            resp.setCreatedAt(OffsetDateTime.now());
            return resp;
        });

        // Test XSS payload in response
        ReviewReplyRequest req = new ReviewReplyRequest("<script>alert('xss')</script> Thank you for your feedback!");

        ReviewReplyResponse reply = reviewService.respondToReview(businessId, userId, "Manager Sarah", reviewId, req, "127.0.0.1", "JUnit");

        assertNotNull(reply);
        assertTrue(review.getAnswered());
        // Assert XSS has been escaped
        assertFalse(reply.getResponseText().contains("<script>"));
        assertTrue(reply.getResponseText().contains("&lt;script&gt;"));
        verify(auditService).record(eq(businessId), eq(userId), eq("REPLY_TO_REVIEW"), any(), any(), any(), any(), any());
    }

    @Test
    void testRespondToReviewAlreadyAnsweredThrowsBadRequest() {
        Review review = Review.builder()
                .id(reviewId)
                .businessId(businessId)
                .customerName("Jane Doe")
                .answered(true)
                .response(ReviewResponse.builder().id(UUID.randomUUID()).build())
                .build();

        when(reviewRepository.findByIdAndBusinessId(reviewId, businessId)).thenReturn(Optional.of(review));

        ReviewReplyRequest req = new ReviewReplyRequest("Another reply");

        assertThrows(BadRequestException.class, () ->
                reviewService.respondToReview(businessId, userId, "Manager", reviewId, req, "127.0.0.1", "JUnit"));
    }

    @Test
    void testRespondToNonExistentReviewThrowsNotFound() {
        when(reviewRepository.findByIdAndBusinessId(reviewId, businessId)).thenReturn(Optional.empty());

        ReviewReplyRequest req = new ReviewReplyRequest("Reply");

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.respondToReview(businessId, userId, "Manager", reviewId, req, "127.0.0.1", "JUnit"));
    }
}
