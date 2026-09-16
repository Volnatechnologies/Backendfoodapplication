package com.caloryhive.business.finance.controller;

import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.finance.dto.CapitalEligibilityResponse;
import com.caloryhive.business.finance.dto.FinancialActivityResponse;
import com.caloryhive.business.finance.dto.FinancialSummaryResponse;
import com.caloryhive.business.finance.dto.RevenueTrendsResponse;
import com.caloryhive.business.finance.service.FinanceService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/business/finance")
@RequiredArgsConstructor
@Tag(name = "Financial Overview", description = "Endpoints for restaurant balances, revenue trends, payouts, activity, and capital eligibility")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN', 'MANAGER')")
    @Operation(summary = "Get financial overview summary", description = "Returns available balance for withdrawal, pending payouts, and monthly target goal progress.")
    public ApiResponse<FinancialSummaryResponse> getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(financeService.getSummary(businessId), "Financial summary fetched successfully");
    }

    @GetMapping("/revenue-trends")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN', 'MANAGER')")
    @Operation(summary = "Get revenue trends chart data", description = "Returns growth over time across all channels for Daily, Weekly, or Monthly periods.")
    public ApiResponse<RevenueTrendsResponse> getRevenueTrends(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "WEEKLY") String filter
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(financeService.getRevenueTrends(businessId, filter), "Revenue trends fetched successfully");
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN', 'MANAGER')")
    @Operation(summary = "Get recent financial activity transactions")
    public ApiResponse<Page<FinancialActivityResponse>> getRecentActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(financeService.getRecentActivity(businessId, pageable), "Recent activity fetched successfully");
    }

    @GetMapping("/recent-activity/export")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Download recent financial activity CSV")
    public void exportActivityCsv(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response
    ) throws IOException {
        UUID businessId = resolveBusinessId(principal);
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recent_activity.csv\"");
        financeService.exportActivityCsv(businessId, response.getWriter());
    }

    @GetMapping("/eligibility")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Check Smart Capital business loan eligibility")
    public ApiResponse<CapitalEligibilityResponse> getEligibility(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(financeService.getEligibility(businessId), "Capital eligibility evaluated successfully");
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
