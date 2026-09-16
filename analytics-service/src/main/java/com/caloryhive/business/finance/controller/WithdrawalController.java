package com.caloryhive.business.finance.controller;

import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.finance.dto.CreateWithdrawalRequest;
import com.caloryhive.business.finance.dto.WithdrawalResponse;
import com.caloryhive.business.finance.service.WithdrawalService;
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
@RequestMapping("/api/business/withdrawals")
@RequiredArgsConstructor
@Tag(name = "Withdrawals", description = "Endpoints for initiating and tracking secure payouts and withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Initiate fund withdrawal", description = "Requests a payout with balance verification, transaction ledger entry, and optional Idempotency-Key.")
    public ApiResponse<WithdrawalResponse> requestWithdrawal(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateWithdrawalRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        WithdrawalResponse response = withdrawalService.requestWithdrawal(
                businessId, userId, request, idempotencyKey,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(response, "Withdrawal request submitted successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN', 'MANAGER')")
    @Operation(summary = "List withdrawals with pagination")
    public ApiResponse<Page<WithdrawalResponse>> getWithdrawals(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(withdrawalService.getWithdrawals(businessId, pageable), "Withdrawals retrieved successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN', 'MANAGER')")
    @Operation(summary = "Get single withdrawal details")
    public ApiResponse<WithdrawalResponse> getWithdrawalById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id
    ) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(withdrawalService.getWithdrawalById(businessId, id), "Withdrawal details retrieved successfully");
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
