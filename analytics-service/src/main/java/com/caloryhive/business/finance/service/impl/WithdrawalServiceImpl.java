package com.caloryhive.business.finance.service.impl;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.InsufficientBalanceException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.finance.dto.CreateWithdrawalRequest;
import com.caloryhive.business.finance.dto.WithdrawalResponse;
import com.caloryhive.business.finance.entity.*;
import com.caloryhive.business.finance.repository.*;
import com.caloryhive.business.finance.service.WithdrawalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private static final Logger log = LoggerFactory.getLogger(WithdrawalServiceImpl.class);
    private static final DateTimeFormatter WTH_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");

    private final WithdrawalRepository withdrawalRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BusinessFinancialAccountRepository financialAccountRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WithdrawalResponse requestWithdrawal(UUID businessId, UUID userId, CreateWithdrawalRequest request, String idempotencyKey, String ipAddress, String userAgent) {
        // 1. Idempotency check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository.findByIdempotencyKeyAndBusinessId(idempotencyKey, businessId);
            if (existingRecord.isPresent()) {
                try {
                    log.info("Returning cached idempotent withdrawal response for key: {}", idempotencyKey);
                    return objectMapper.readValue(existingRecord.get().getResponsePayload(), WithdrawalResponse.class);
                } catch (Exception e) {
                    log.warn("Could not deserialize idempotent response, re-verifying withdrawal: {}", e.getMessage());
                }
            }

            Optional<Withdrawal> existingWth = withdrawalRepository.findByIdempotencyKey(idempotencyKey);
            if (existingWth.isPresent()) {
                return mapToDto(existingWth.get());
            }
        }

        // 2. Minimum amount validation
        if (request.getAmount() == null || request.getAmount().compareTo(new BigDecimal("100.00")) < 0) {
            throw new BadRequestException("Minimum withdrawal amount is $100.00");
        }

        // 3. Bank account verification & tenant isolation
        BankAccount bankAccount = bankAccountRepository.findByIdAndBusinessId(request.getBankAccountId(), businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found or does not belong to your business"));

        // 4. Lock financial account & verify available balance
        BusinessFinancialAccount account = financialAccountRepository.findByBusinessIdWithLock(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial account not found for business"));

        if (account.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient available balance. Requested: $" + request.getAmount() +
                    ", Available: $" + account.getAvailableBalance());
        }

        // 5. Deduct available balance atomically
        account.setAvailableBalance(account.getAvailableBalance().subtract(request.getAmount()));
        financialAccountRepository.save(account);

        // 6. Create withdrawal entity
        String reference = "WTH-" + (System.currentTimeMillis() % 100000);
        Withdrawal withdrawal = Withdrawal.builder()
                .businessId(businessId)
                .bankAccount(bankAccount)
                .amount(request.getAmount())
                .status(WithdrawalStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .transactionReference(reference)
                .build();

        Withdrawal saved = withdrawalRepository.save(withdrawal);

        // 7. Record financial transaction ledger entry
        FinancialTransaction transaction = FinancialTransaction.builder()
                .businessId(businessId)
                .type("WITHDRAWAL")
                .amount(request.getAmount().negate())
                .status("COMPLETED")
                .description("Withdrawal to Bank (" + bankAccount.getMaskedAccountNumber().replace("****", "...") + ")")
                .referenceId(reference)
                .build();
        transactionRepository.save(transaction);

        // 8. Record audit log
        auditService.record(businessId, userId, "REQUEST_WITHDRAWAL", "Withdrawal", saved.getId().toString(),
                "Requested withdrawal of $" + saved.getAmount() + " to " + bankAccount.getBankName() + " (" + bankAccount.getMaskedAccountNumber() + ")",
                ipAddress, userAgent);

        WithdrawalResponse responseDto = mapToDto(saved);

        // 9. Save idempotency record if key was provided
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                String payload = objectMapper.writeValueAsString(responseDto);
                IdempotencyRecord record = IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .businessId(businessId)
                        .requestHash(idempotencyKey)
                        .responsePayload(payload)
                        .statusCode(200)
                        .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1))
                        .build();
                idempotencyRecordRepository.save(record);
            } catch (Exception e) {
                log.warn("Could not save idempotency record: {}", e.getMessage());
            }
        }

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WithdrawalResponse> getWithdrawals(UUID businessId, Pageable pageable) {
        return withdrawalRepository.findByBusinessIdOrderByCreatedAtDesc(businessId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public WithdrawalResponse getWithdrawalById(UUID businessId, UUID withdrawalId) {
        Withdrawal withdrawal = withdrawalRepository.findByIdAndBusinessId(withdrawalId, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found with id: " + withdrawalId));
        return mapToDto(withdrawal);
    }

    private WithdrawalResponse mapToDto(Withdrawal w) {
        return WithdrawalResponse.builder()
                .id(w.getId())
                .bankAccountId(w.getBankAccount() != null ? w.getBankAccount().getId() : null)
                .bankName(w.getBankAccount() != null ? w.getBankAccount().getBankName() : "Bank Account")
                .maskedAccountNumber(w.getBankAccount() != null ? w.getBankAccount().getMaskedAccountNumber() : "**** 0000")
                .amount(w.getAmount())
                .status(w.getStatus() != null ? w.getStatus().name() : "PENDING")
                .transactionReference(w.getTransactionReference())
                .idempotencyKey(w.getIdempotencyKey())
                .createdAt(w.getCreatedAt())
                .formattedDate(w.getCreatedAt() != null ? w.getCreatedAt().format(WTH_DATE_FORMAT) : "")
                .build();
    }
}
