package com.caloryhive.business.security;

import com.caloryhive.business.finance.dto.BankAccountRequest;
import com.caloryhive.business.finance.dto.CreateWithdrawalRequest;
import com.caloryhive.business.finance.service.BankAccountService;
import com.caloryhive.business.finance.service.WithdrawalService;
import com.caloryhive.business.reviews.service.ReviewService;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TenantIsolationSecurityTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private WithdrawalService withdrawalService;

    private UUID businessA;
    private UUID businessB;
    private UUID userA;

    @BeforeEach
    void setUp() {
        businessA = UUID.randomUUID();
        businessB = UUID.randomUUID();
        userA = UUID.randomUUID();
    }

    @Test
    void testUserFromBusinessACannotAccessBusinessBReview() {
        UUID businessBReviewId = UUID.randomUUID();

        // Attempting to read Business B's review using Business A's tenant context must fail with ResourceNotFoundException (404)
        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.getReviewById(businessA, businessBReviewId));
    }

    @Test
    void testUserFromBusinessACannotModifyBusinessBBankAccount() {
        UUID businessBBankAccountId = UUID.randomUUID();
        BankAccountRequest req = BankAccountRequest.builder()
                .bankName("Attacker Bank")
                .accountHolderName("Hacker")
                .accountNumber("1234567890")
                .routingNumber("000000000")
                .build();

        // Attempting to update or delete Business B's bank account with Business A's context must fail
        assertThrows(ResourceNotFoundException.class, () ->
                bankAccountService.updateBankAccount(businessA, userA, businessBBankAccountId, req, "127.0.0.1", "JUnit"));

        assertThrows(ResourceNotFoundException.class, () ->
                bankAccountService.deleteBankAccount(businessA, userA, businessBBankAccountId, "127.0.0.1", "JUnit"));
    }

    @Test
    void testUserFromBusinessACannotWithdrawUsingBusinessBBankAccount() {
        UUID businessBBankAccountId = UUID.randomUUID();
        CreateWithdrawalRequest req = new CreateWithdrawalRequest(new BigDecimal("500.00"), businessBBankAccountId);

        // Attempting to withdraw using another business's bank account must fail
        assertThrows(ResourceNotFoundException.class, () ->
                withdrawalService.requestWithdrawal(businessA, userA, req, null, "127.0.0.1", "JUnit"));
    }
}
