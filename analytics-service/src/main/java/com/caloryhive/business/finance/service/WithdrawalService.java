package com.caloryhive.business.finance.service;

import com.caloryhive.business.finance.dto.CreateWithdrawalRequest;
import com.caloryhive.business.finance.dto.WithdrawalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WithdrawalService {

    WithdrawalResponse requestWithdrawal(UUID businessId, UUID userId, CreateWithdrawalRequest request, String idempotencyKey, String ipAddress, String userAgent);

    Page<WithdrawalResponse> getWithdrawals(UUID businessId, Pageable pageable);

    WithdrawalResponse getWithdrawalById(UUID businessId, UUID withdrawalId);
}
