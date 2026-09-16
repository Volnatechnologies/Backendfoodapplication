package com.caloryhive.business.rewards;

import com.caloryhive.business.rewards.controller.RewardsController;
import com.caloryhive.business.rewards.dto.*;
import com.caloryhive.business.rewards.service.RewardsService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardsController.class)
@AutoConfigureMockMvc(addFilters = false)
class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardsService rewardsService;

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
        RewardSummaryResponse summary = RewardSummaryResponse.builder()
                .currentTierName("PLATINUM")
                .currentTierDisplay("Platinum Partner")
                .totalPointsEarned(12450)
                .currentPointsBalance(12450)
                .nextTierName("Diamond")
                .nextTierPointsThreshold(15000)
                .pointsToNextTier(2550)
                .tierProgressPercent(83.0)
                .currentPerks(Collections.emptyList())
                .build();

        when(rewardsService.getSummary(any(UUID.class))).thenReturn(summary);

        mockMvc.perform(get("/api/business/rewards/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentTierName").value("PLATINUM"))
                .andExpect(jsonPath("$.data.currentTierDisplay").value("Platinum Partner"))
                .andExpect(jsonPath("$.data.totalPointsEarned").value(12450))
                .andExpect(jsonPath("$.data.pointsToNextTier").value(2550));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetCatalog() throws Exception {
        RewardCatalogItemResponse item = RewardCatalogItemResponse.builder()
                .id(UUID.randomUUID())
                .title("Pro Photoshoot")
                .pointsCost(5000)
                .category("MARKETING")
                .isActive(true)
                .build();

        when(rewardsService.getCatalog()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/business/rewards/catalog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Pro Photoshoot"))
                .andExpect(jsonPath("$.data[0].pointsCost").value(5000));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testRedeemReward() throws Exception {
        UUID itemId = UUID.randomUUID();
        RedeemRewardRequest req = new RedeemRewardRequest(itemId);
        RedemptionResponse resp = RedemptionResponse.builder()
                .id(UUID.randomUUID())
                .catalogItemId(itemId)
                .itemTitle("Pro Photoshoot")
                .pointsSpent(5000)
                .remainingPointsBalance(7450)
                .status("COMPLETED")
                .redeemedAt(OffsetDateTime.now())
                .build();

        when(rewardsService.redeemReward(any(), any(), any(), any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/business/rewards/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.itemTitle").value("Pro Photoshoot"))
                .andExpect(jsonPath("$.data.pointsSpent").value(5000))
                .andExpect(jsonPath("$.data.remainingPointsBalance").value(7450));
    }
}
