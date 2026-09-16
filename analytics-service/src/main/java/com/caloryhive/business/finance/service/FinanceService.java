package com.caloryhive.business.finance.service;

import com.caloryhive.business.finance.dto.CapitalEligibilityResponse;
import com.caloryhive.business.finance.dto.FinancialActivityResponse;
import com.caloryhive.business.finance.dto.FinancialSummaryResponse;
import com.caloryhive.business.finance.dto.RevenueTrendsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.PrintWriter;
import java.util.UUID;

public interface FinanceService {

    FinancialSummaryResponse getSummary(UUID businessId);

    RevenueTrendsResponse getRevenueTrends(UUID businessId, String filter);

    Page<FinancialActivityResponse> getRecentActivity(UUID businessId, Pageable pageable);

    void exportActivityCsv(UUID businessId, PrintWriter writer);

    CapitalEligibilityResponse getEligibility(UUID businessId);
}
