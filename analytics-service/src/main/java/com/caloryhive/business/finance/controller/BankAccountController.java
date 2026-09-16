package com.caloryhive.business.finance.controller;

import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.finance.dto.BankAccountRequest;
import com.caloryhive.business.finance.dto.BankAccountResponse;
import com.caloryhive.business.finance.service.BankAccountService;
import com.caloryhive.business.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business/bank-accounts")
@RequiredArgsConstructor
@Tag(name = "Bank Accounts", description = "Endpoints for managing business verified bank accounts with tokenized masking")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "List verified business bank accounts", description = "Returns bank accounts with masked numbers (e.g. **** 4210) for withdrawal selection.")
    public ApiResponse<List<BankAccountResponse>> getBankAccounts(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(bankAccountService.getBankAccounts(businessId), "Bank accounts retrieved successfully");
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Add a new verified bank account", description = "Validates and tokenizes bank details without exposing plaintext credentials.")
    public ApiResponse<BankAccountResponse> addBankAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BankAccountRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        BankAccountResponse response = bankAccountService.addBankAccount(
                businessId, userId, request,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(response, "Bank account added successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Update an existing bank account")
    public ApiResponse<BankAccountResponse> updateBankAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody BankAccountRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        BankAccountResponse response = bankAccountService.updateBankAccount(
                businessId, userId, id, request,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(response, "Bank account updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'FINANCE_ADMIN')")
    @Operation(summary = "Delete a bank account")
    public ApiResponse<Void> deleteBankAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        bankAccountService.deleteBankAccount(
                businessId, userId, id,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(null, "Bank account deleted successfully");
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
