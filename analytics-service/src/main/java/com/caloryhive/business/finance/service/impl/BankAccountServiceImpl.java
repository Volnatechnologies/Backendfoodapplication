package com.caloryhive.business.finance.service.impl;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.finance.dto.BankAccountRequest;
import com.caloryhive.business.finance.dto.BankAccountResponse;
import com.caloryhive.business.finance.entity.BankAccount;
import com.caloryhive.business.finance.repository.BankAccountRepository;
import com.caloryhive.business.finance.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getBankAccounts(UUID businessId) {
        List<BankAccount> accounts = bankAccountRepository.findByBusinessIdOrderByIsDefaultDescCreatedAtDesc(businessId);
        return accounts.stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional
    public BankAccountResponse addBankAccount(UUID businessId, UUID userId, BankAccountRequest request, String ipAddress, String userAgent) {
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAccounts(businessId);
        }

        String masked = maskAccountNumber(request.getAccountNumber());

        BankAccount account = BankAccount.builder()
                .businessId(businessId)
                .bankName(request.getBankName().trim())
                .accountHolderName(request.getAccountHolderName().trim())
                .accountType(request.getAccountType() != null ? request.getAccountType() : "Checking Account")
                .maskedAccountNumber(masked)
                .routingNumber(request.getRoutingNumber() != null ? request.getRoutingNumber().trim() : null)
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .isVerified(true)
                .build();

        BankAccount saved = bankAccountRepository.save(account);

        auditService.record(businessId, userId, "ADD_BANK_ACCOUNT", "BankAccount", saved.getId().toString(),
                "Added bank account: " + saved.getBankName() + " (" + masked + ")", ipAddress, userAgent);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public BankAccountResponse updateBankAccount(UUID businessId, UUID userId, UUID accountId, BankAccountRequest request, String ipAddress, String userAgent) {
        BankAccount account = bankAccountRepository.findByIdAndBusinessId(accountId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + accountId));

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(account.getIsDefault())) {
            unsetDefaultAccounts(businessId);
            account.setIsDefault(true);
        }

        if (request.getBankName() != null && !request.getBankName().isBlank()) {
            account.setBankName(request.getBankName().trim());
        }
        if (request.getAccountHolderName() != null && !request.getAccountHolderName().isBlank()) {
            account.setAccountHolderName(request.getAccountHolderName().trim());
        }
        if (request.getAccountNumber() != null && !request.getAccountNumber().isBlank()) {
            account.setMaskedAccountNumber(maskAccountNumber(request.getAccountNumber()));
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }

        BankAccount updated = bankAccountRepository.save(account);

        auditService.record(businessId, userId, "UPDATE_BANK_ACCOUNT", "BankAccount", updated.getId().toString(),
                "Updated bank account: " + updated.getBankName(), ipAddress, userAgent);

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteBankAccount(UUID businessId, UUID userId, UUID accountId, String ipAddress, String userAgent) {
        BankAccount account = bankAccountRepository.findByIdAndBusinessId(accountId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + accountId));

        bankAccountRepository.delete(account);

        auditService.record(businessId, userId, "DELETE_BANK_ACCOUNT", "BankAccount", accountId.toString(),
                "Deleted bank account: " + account.getBankName() + " (" + account.getMaskedAccountNumber() + ")", ipAddress, userAgent);
    }

    private void unsetDefaultAccounts(UUID businessId) {
        List<BankAccount> accounts = bankAccountRepository.findByBusinessIdOrderByIsDefaultDescCreatedAtDesc(businessId);
        for (BankAccount a : accounts) {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                bankAccountRepository.save(a);
            }
        }
    }

    private String maskAccountNumber(String raw) {
        if (raw == null || raw.length() < 4) return "**** 0000";
        String lastFour = raw.substring(raw.length() - 4);
        return "**** " + lastFour;
    }

    private BankAccountResponse mapToDto(BankAccount a) {
        return BankAccountResponse.builder()
                .id(a.getId())
                .bankName(a.getBankName())
                .accountHolderName(a.getAccountHolderName())
                .accountType(a.getAccountType())
                .maskedAccountNumber(a.getMaskedAccountNumber())
                .isDefault(a.getIsDefault())
                .isVerified(a.getIsVerified())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
