package com.caloryhive.business.finance;

import com.caloryhive.business.finance.controller.FinanceController;
import com.caloryhive.business.finance.dto.CapitalEligibilityResponse;
import com.caloryhive.business.finance.dto.FinancialSummaryResponse;
import com.caloryhive.business.finance.dto.RevenueTrendsResponse;
import com.caloryhive.business.finance.service.FinanceService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FinanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceService financeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.caloryhive.business.business.repository.BusinessRepository businessRepository;

    @Test
    @WithMockUser(roles = "FINANCE_ADMIN")
    void testGetSummary() throws Exception {
        FinancialSummaryResponse summary = FinancialSummaryResponse.builder()
                .availableForWithdrawal(new BigDecimal("14285.50"))
                .balanceGrowthPercent(12.0)
                .pendingPayouts(new BigDecimal("3412.00"))
                .monthlyGoal(new BigDecimal("42500.00"))
                .monthlyGoalAchieved(new BigDecimal("27625.00"))
                .monthlyProgressPercent(65.0)
                .currency("USD")
                .nextPayoutDate(LocalDate.of(2023, 10, 24))
                .build();

        when(financeService.getSummary(any(UUID.class))).thenReturn(summary);

        mockMvc.perform(get("/api/business/finance/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.availableForWithdrawal").value(14285.50))
                .andExpect(jsonPath("$.data.pendingPayouts").value(3412.00))
                .andExpect(jsonPath("$.data.monthlyGoal").value(42500.00))
                .andExpect(jsonPath("$.data.monthlyProgressPercent").value(65.0));
    }

    @Test
    @WithMockUser(roles = "FINANCE_ADMIN")
    void testGetRevenueTrends() throws Exception {
        RevenueTrendsResponse trends = RevenueTrendsResponse.builder()
                .filter("WEEKLY")
                .totalRevenue(new BigDecimal("36600.00"))
                .points(Collections.emptyList())
                .build();

        when(financeService.getRevenueTrends(any(UUID.class), eq("WEEKLY"))).thenReturn(trends);

        mockMvc.perform(get("/api/business/finance/revenue-trends?filter=WEEKLY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.filter").value("WEEKLY"))
                .andExpect(jsonPath("$.data.totalRevenue").value(36600.00));
    }

    @Test
    @WithMockUser(roles = "FINANCE_ADMIN")
    void testGetEligibility() throws Exception {
        CapitalEligibilityResponse eligibility = CapitalEligibilityResponse.builder()
                .eligible(true)
                .maxLoanAmount(new BigDecimal("25000.00"))
                .interestRatePercent(BigDecimal.ZERO)
                .zeroInterestDays(90)
                .qualificationBasis("Based on your consistent order volume and earnings")
                .build();

        when(financeService.getEligibility(any(UUID.class))).thenReturn(eligibility);

        mockMvc.perform(get("/api/business/finance/eligibility")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eligible").value(true))
                .andExpect(jsonPath("$.data.maxLoanAmount").value(25000.00))
                .andExpect(jsonPath("$.data.zeroInterestDays").value(90));
    }
}
