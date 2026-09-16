package com.caloryhive.business.finance;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.InsufficientBalanceException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.finance.dto.CreateWithdrawalRequest;
import com.caloryhive.business.finance.dto.WithdrawalResponse;
import com.caloryhive.business.finance.entity.BankAccount;
import com.caloryhive.business.finance.entity.BusinessFinancialAccount;
import com.caloryhive.business.finance.entity.IdempotencyRecord;
import com.caloryhive.business.finance.entity.Withdrawal;
import com.caloryhive.business.finance.repository.*;
import com.caloryhive.business.finance.service.impl.WithdrawalServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BusinessFinancialAccountRepository financialAccountRepository;

    @Mock
    private FinancialTransactionRepository transactionRepository;

    @Mock
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Mock
    private AuditService auditService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private WithdrawalServiceImpl withdrawalService;

    private UUID businessId;
    private UUID userId;
    private UUID bankAccountId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        userId = UUID.randomUUID();
        bankAccountId = UUID.randomUUID();
    }

    @Test
    void testRequestWithdrawalSuccess() {
        BankAccount bankAccount = BankAccount.builder()
                .id(bankAccountId)
                .businessId(businessId)
                .bankName("Chase Business Platinum")
                .maskedAccountNumber("**** 4210")
                .build();

        BusinessFinancialAccount finAccount = BusinessFinancialAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .availableBalance(new BigDecimal("14285.50"))
                .build();

        when(bankAccountRepository.findByIdAndBusinessId(bankAccountId, businessId)).thenReturn(Optional.of(bankAccount));
        when(financialAccountRepository.findByBusinessIdWithLock(businessId)).thenReturn(Optional.of(finAccount));
        when(withdrawalRepository.save(any(Withdrawal.class))).thenAnswer(inv -> {
            Withdrawal w = inv.getArgument(0);
            w.setId(UUID.randomUUID());
            w.setCreatedAt(OffsetDateTime.now());
            return w;
        });

        CreateWithdrawalRequest req = new CreateWithdrawalRequest(new BigDecimal("1000.00"), bankAccountId);

        WithdrawalResponse resp = withdrawalService.requestWithdrawal(businessId, userId, req, "key-123", "127.0.0.1", "JUnit");

        assertNotNull(resp);
        assertEquals(new BigDecimal("1000.00"), resp.getAmount());
        assertEquals("Chase Business Platinum", resp.getBankName());
        assertEquals("**** 4210", resp.getMaskedAccountNumber());
        // Verify balance was deducted
        assertEquals(new BigDecimal("13285.50"), finAccount.getAvailableBalance());
        verify(transactionRepository).save(any());
        verify(auditService).record(eq(businessId), eq(userId), eq("REQUEST_WITHDRAWAL"), any(), any(), any(), any(), any());
    }

    @Test
    void testRequestWithdrawalInsufficientBalanceThrowsException() {
        BankAccount bankAccount = BankAccount.builder()
                .id(bankAccountId)
                .businessId(businessId)
                .bankName("Chase")
                .maskedAccountNumber("**** 4210")
                .build();

        BusinessFinancialAccount finAccount = BusinessFinancialAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .availableBalance(new BigDecimal("500.00"))
                .build();

        when(bankAccountRepository.findByIdAndBusinessId(bankAccountId, businessId)).thenReturn(Optional.of(bankAccount));
        when(financialAccountRepository.findByBusinessIdWithLock(businessId)).thenReturn(Optional.of(finAccount));

        CreateWithdrawalRequest req = new CreateWithdrawalRequest(new BigDecimal("1000.00"), bankAccountId);

        assertThrows(InsufficientBalanceException.class, () ->
                withdrawalService.requestWithdrawal(businessId, userId, req, null, "127.0.0.1", "JUnit"));
    }

    @Test
    void testRequestWithdrawalBelowMinimumThrowsException() {
        CreateWithdrawalRequest req = new CreateWithdrawalRequest(new BigDecimal("50.00"), bankAccountId);

        assertThrows(BadRequestException.class, () ->
                withdrawalService.requestWithdrawal(businessId, userId, req, null, "127.0.0.1", "JUnit"));
    }

    @Test
    void testRequestWithdrawalIdempotencyReturnsCachedResponse() throws Exception {
        WithdrawalResponse cached = WithdrawalResponse.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("1000.00"))
                .bankName("Chase")
                .status("PENDING")
                .idempotencyKey("idemp-unique-999")
                .build();

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey("idemp-unique-999")
                .businessId(businessId)
                .responsePayload(objectMapper.writeValueAsString(cached))
                .build();

        when(idempotencyRecordRepository.findByIdempotencyKeyAndBusinessId("idemp-unique-999", businessId))
                .thenReturn(Optional.of(record));

        CreateWithdrawalRequest req = new CreateWithdrawalRequest(new BigDecimal("1000.00"), bankAccountId);

        WithdrawalResponse result = withdrawalService.requestWithdrawal(businessId, userId, req, "idemp-unique-999", "127.0.0.1", "JUnit");

        assertNotNull(result);
        assertEquals(cached.getId(), result.getId());
        verify(financialAccountRepository, never()).findByBusinessIdWithLock(any());
    }
}
