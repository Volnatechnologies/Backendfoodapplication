package com.caloryhive.business.reviews;

import com.caloryhive.business.reviews.controller.ReviewController;
import com.caloryhive.business.reviews.dto.RatingDistributionResponse;
import com.caloryhive.business.reviews.dto.ReviewReplyRequest;
import com.caloryhive.business.reviews.dto.ReviewReplyResponse;
import com.caloryhive.business.reviews.dto.ReviewSummaryResponse;
import com.caloryhive.business.reviews.service.ReviewService;
import com.caloryhive.business.security.JwtAuthenticationFilter;
import com.caloryhive.business.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.caloryhive.business.business.repository.BusinessRepository businessRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetSummary() throws Exception {
        ReviewSummaryResponse summary = ReviewSummaryResponse.builder()
                .averageRating(4.8)
                .totalReviews(1420L)
                .responseRate(98.0)
                .sentimentScore(92.0)
                .build();

        when(reviewService.getSummary(any(UUID.class), eq("30D"))).thenReturn(summary);

        mockMvc.perform(get("/api/business/reviews/summary?period=30D")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").value(4.8))
                .andExpect(jsonPath("$.data.totalReviews").value(1420))
                .andExpect(jsonPath("$.data.responseRate").value(98.0))
                .andExpect(jsonPath("$.data.sentimentScore").value(92.0));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetDistribution() throws Exception {
        RatingDistributionResponse dist = RatingDistributionResponse.builder()
                .totalReviews(1420L)
                .distribution(List.of(
                        new RatingDistributionResponse.StarRatingCount(5, 1150L, 81.0),
                        new RatingDistributionResponse.StarRatingCount(4, 190L, 13.4),
                        new RatingDistributionResponse.StarRatingCount(3, 50L, 3.5),
                        new RatingDistributionResponse.StarRatingCount(2, 20L, 1.4),
                        new RatingDistributionResponse.StarRatingCount(1, 10L, 0.7)
                ))
                .build();

        when(reviewService.getDistribution(any(UUID.class))).thenReturn(dist);

        mockMvc.perform(get("/api/business/reviews/distribution")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalReviews").value(1420))
                .andExpect(jsonPath("$.data.distribution[0].stars").value(5))
                .andExpect(jsonPath("$.data.distribution[0].percentage").value(81.0));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testReplyToReview() throws Exception {
        UUID reviewId = UUID.randomUUID();
        ReviewReplyRequest req = new ReviewReplyRequest("Thank you for your wonderful review!");
        ReviewReplyResponse reply = ReviewReplyResponse.builder()
                .id(UUID.randomUUID())
                .reviewId(reviewId)
                .responderName("Manager")
                .responseText("Thank you for your wonderful review!")
                .createdAt(OffsetDateTime.now())
                .build();

        when(reviewService.respondToReview(any(), any(), any(), eq(reviewId), any(), any(), any())).thenReturn(reply);

        mockMvc.perform(post("/api/business/reviews/" + reviewId + "/response")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.responseText").value("Thank you for your wonderful review!"));
    }
}
