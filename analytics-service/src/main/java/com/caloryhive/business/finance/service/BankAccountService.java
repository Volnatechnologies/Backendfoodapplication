package com.caloryhive.business.finance.service;

import com.caloryhive.business.finance.dto.BankAccountRequest;
import com.caloryhive.business.finance.dto.BankAccountResponse;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {

    List<BankAccountResponse> getBankAccounts(UUID businessId);

    BankAccountResponse addBankAccount(UUID businessId, UUID userId, BankAccountRequest request, String ipAddress, String userAgent);

    BankAccountResponse updateBankAccount(UUID businessId, UUID userId, UUID accountId, BankAccountRequest request, String ipAddress, String userAgent);

    void deleteBankAccount(UUID businessId, UUID userId, UUID accountId, String ipAddress, String userAgent);
}
