package com.caloryhive.business.analytics;

import com.caloryhive.business.analytics.controller.AnalyticsController;
import com.caloryhive.business.analytics.dto.AnalyticsSummaryResponse;
import com.caloryhive.business.analytics.dto.EarningsOverviewResponse;
import com.caloryhive.business.analytics.dto.OrderChannelDistributionResponse;
import com.caloryhive.business.analytics.service.AnalyticsService;
import com.caloryhive.business.security.JwtAuthenticationFilter;
import com.caloryhive.business.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.caloryhive.business.business.repository.BusinessRepository businessRepository;

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetSummary() throws Exception {
        AnalyticsSummaryResponse summary = AnalyticsSummaryResponse.builder()
                .totalOrders(1248L)
                .totalRevenue(new BigDecimal("42500.00"))
                .averageOrderValue(new BigDecimal("35.05"))
                .customerSatisfaction(4.8)
                .period("30D")
                .build();

        when(analyticsService.getSummary(any(UUID.class), eq("30D"))).thenReturn(summary);

        mockMvc.perform(get("/api/business/analytics/summary?period=30D")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalOrders").value(1248))
                .andExpect(jsonPath("$.data.totalRevenue").value(42500.00))
                .andExpect(jsonPath("$.data.averageOrderValue").value(35.05))
                .andExpect(jsonPath("$.data.customerSatisfaction").value(4.8));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetEarnings() throws Exception {
        EarningsOverviewResponse earnings = EarningsOverviewResponse.builder()
                .period("7D")
                .totalEarnings(new BigDecimal("42500.00"))
                .totalOrders(1248L)
                .points(Collections.emptyList())
                .build();

        when(analyticsService.getEarnings(any(UUID.class), eq("7D"))).thenReturn(earnings);

        mockMvc.perform(get("/api/business/analytics/earnings?period=7D")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.period").value("7D"))
                .andExpect(jsonPath("$.data.totalEarnings").value(42500.00));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void testGetOrderChannels() throws Exception {
        OrderChannelDistributionResponse response = OrderChannelDistributionResponse.builder()
                .totalOrders(1200L)
                .channels(List.of(
                        new OrderChannelDistributionResponse.ChannelItem("Dine-in", 540L, 45.0, "#3B82F6"),
                        new OrderChannelDistributionResponse.ChannelItem("Delivery", 480L, 40.0, "#10B981"),
                        new OrderChannelDistributionResponse.ChannelItem("Catering", 180L, 15.0, "#F59E0B")
                ))
                .build();

        when(analyticsService.getOrderChannels(any(UUID.class), eq("30D"))).thenReturn(response);

        mockMvc.perform(get("/api/business/analytics/order-channels?period=30D")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalOrders").value(1200))
                .andExpect(jsonPath("$.data.channels[0].name").value("Dine-in"))
                .andExpect(jsonPath("$.data.channels[0].percentage").value(45.0));
    }
}
