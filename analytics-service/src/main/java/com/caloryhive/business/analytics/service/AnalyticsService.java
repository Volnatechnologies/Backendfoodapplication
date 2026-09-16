package com.caloryhive.business.analytics.service;

import com.caloryhive.business.analytics.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public interface AnalyticsService {

    AnalyticsSummaryResponse getSummary(UUID businessId, String period);

    EarningsOverviewResponse getEarnings(UUID businessId, String period);

    Page<RecentTransactionResponse> getRecentTransactions(UUID businessId, String search, Pageable pageable);

    List<TopSellingItemResponse> getTopSellingItems(UUID businessId, int limit);

    OrderChannelDistributionResponse getOrderChannels(UUID businessId, String period);

    LiveInsightsResponse getLiveInsights(UUID businessId);

    void exportReportCsv(UUID businessId, String period, PrintWriter writer);
}
