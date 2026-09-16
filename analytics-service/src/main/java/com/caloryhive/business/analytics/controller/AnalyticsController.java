package com.caloryhive.business.analytics.controller;

import com.caloryhive.business.analytics.dto.*;
import com.caloryhive.business.analytics.service.AnalyticsService;
import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Insights", description = "Endpoints for restaurant analytics, earnings overview, orders, and sales metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get analytics summary metrics", description = "Calculates total orders, total revenue, average order value, satisfaction, and growth deltas.")
    public ApiResponse<AnalyticsSummaryResponse> getSummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "30D") String period
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(analyticsService.getSummary(businessId, period), "Analytics summary fetched successfully");
    }

    @GetMapping("/earnings")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get earnings overview chart data", description = "Returns daily earnings data points for 7D, 30D, or YTD charts.")
    public ApiResponse<EarningsOverviewResponse> getEarnings(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "7D") String period
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(analyticsService.getEarnings(businessId, period), "Earnings overview fetched successfully");
    }

    @GetMapping("/recent-transactions")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get recent transactions", description = "Returns paginated list of recent orders with customer and amount info.")
    public ApiResponse<Page<RecentTransactionResponse>> getRecentTransactions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(analyticsService.getRecentTransactions(businessId, search, pageable), "Recent transactions fetched successfully");
    }

    @GetMapping("/top-selling-items")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get top selling items", description = "Aggregates most ordered items with category, quantities, and revenue.")
    public ApiResponse<List<TopSellingItemResponse>> getTopSellingItems(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "5") int limit
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(analyticsService.getTopSellingItems(businessId, limit), "Top selling items fetched successfully");
    }

    @GetMapping("/order-channels")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get order channel distribution", description = "Calculates orders distribution across Dine-in, Delivery, and Catering.")
    public ApiResponse<OrderChannelDistributionResponse> getOrderChannels(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "30D") String period
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(analyticsService.getOrderChannels(businessId, period), "Order channels fetched successfully");
    }

    @GetMapping("/live-insights")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get live operational insights", description = "Returns peak busy hours and average kitchen fulfillment time.")
    public ApiResponse<LiveInsightsResponse> getLiveInsights(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(analyticsService.getLiveInsights(businessId), "Live insights fetched successfully");
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Export analytics report as CSV", description = "Generates and streams a CSV report for the selected period.")
    public void exportReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "30D") String period,
            HttpServletResponse response
    ) throws IOException {
        UUID businessId = resolveBusinessId(principal);
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"analytics_report_" + period.toLowerCase() + ".csv\"");
        analyticsService.exportReportCsv(businessId, period, response.getWriter());
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
