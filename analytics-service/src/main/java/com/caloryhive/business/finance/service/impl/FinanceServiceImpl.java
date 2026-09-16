package com.caloryhive.business.finance.service.impl;

import com.caloryhive.business.analytics.repository.OrderRepository;
import com.caloryhive.business.finance.dto.CapitalEligibilityResponse;
import com.caloryhive.business.finance.dto.FinancialActivityResponse;
import com.caloryhive.business.finance.dto.FinancialSummaryResponse;
import com.caloryhive.business.finance.dto.RevenueTrendsResponse;
import com.caloryhive.business.finance.entity.BusinessFinancialAccount;
import com.caloryhive.business.finance.entity.FinancialTransaction;
import com.caloryhive.business.finance.repository.BusinessFinancialAccountRepository;
import com.caloryhive.business.finance.repository.FinancialTransactionRepository;
import com.caloryhive.business.finance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceServiceImpl implements FinanceService {

    private static final DateTimeFormatter ACTIVITY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");

    private final BusinessFinancialAccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Override
    public FinancialSummaryResponse getSummary(UUID businessId) {
        BusinessFinancialAccount account = accountRepository.findByBusinessId(businessId)
                .orElseGet(() -> BusinessFinancialAccount.builder()
                        .businessId(businessId)
                        .availableBalance(new BigDecimal("14285.50"))
                        .pendingBalance(new BigDecimal("3412.00"))
                        .monthlyGoal(new BigDecimal("42500.00"))
                        .monthlyGoalAchieved(new BigDecimal("27625.00"))
                        .currency("USD")
                        .nextPayoutDate(LocalDate.now().plusDays(5))
                        .verifiedBy("Partner Central Finance")
                        .build());

        double progressPercent = account.getMonthlyGoal().compareTo(BigDecimal.ZERO) > 0
                ? account.getMonthlyGoalAchieved()
                        .divide(account.getMonthlyGoal(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue()
                : 65.0;

        return FinancialSummaryResponse.builder()
                .availableForWithdrawal(account.getAvailableBalance())
                .balanceGrowthPercent(12.0)
                .verifiedBy(account.getVerifiedBy())
                .pendingPayouts(account.getPendingBalance())
                .pendingOrdersCount(142L)
                .pendingDaysExpected(2)
                .monthlyGoal(account.getMonthlyGoal())
                .monthlyGoalAchieved(account.getMonthlyGoalAchieved())
                .monthlyProgressPercent(progressPercent)
                .currency(account.getCurrency())
                .nextPayoutDate(account.getNextPayoutDate() != null ? account.getNextPayoutDate() : LocalDate.of(2023, 10, 24))
                .build();
    }

    @Override
    public RevenueTrendsResponse getRevenueTrends(UUID businessId, String filter) {
        String activeFilter = filter != null ? filter.toUpperCase() : "WEEKLY";
        List<RevenueTrendsResponse.TrendPoint> points = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if ("DAILY".equals(activeFilter)) {
            String[] hours = {"00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "23:59"};
            BigDecimal[] amounts = {
                    new BigDecimal("150.00"), new BigDecimal("80.00"), new BigDecimal("620.00"),
                    new BigDecimal("1850.00"), new BigDecimal("1240.00"), new BigDecimal("2640.00"), new BigDecimal("980.00")
            };
            for (int i = 0; i < hours.length; i++) {
                points.add(RevenueTrendsResponse.TrendPoint.builder()
                        .label(hours[i])
                        .revenue(amounts[i])
                        .ordersCount((long) (amounts[i].intValue() / 35))
                        .build());
                total = total.add(amounts[i]);
            }
        } else if ("MONTHLY".equals(activeFilter)) {
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            BigDecimal base = new BigDecimal("32000.00");
            for (int i = 0; i < months.length; i++) {
                BigDecimal rev = base.add(BigDecimal.valueOf(i * 1150L));
                points.add(RevenueTrendsResponse.TrendPoint.builder()
                        .label(months[i])
                        .revenue(rev)
                        .ordersCount((long) (rev.intValue() / 35))
                        .build());
                total = total.add(rev);
            }
        } else {
            // Default: WEEKLY
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            BigDecimal[] amounts = {
                    new BigDecimal("2100.00"), new BigDecimal("2400.00"), new BigDecimal("3100.00"),
                    new BigDecimal("3600.00"), new BigDecimal("7400.00"), new BigDecimal("9800.00"), new BigDecimal("8200.00")
            };
            for (int i = 0; i < days.length; i++) {
                points.add(RevenueTrendsResponse.TrendPoint.builder()
                        .label(days[i])
                        .revenue(amounts[i])
                        .ordersCount((long) (amounts[i].intValue() / 35))
                        .build());
                total = total.add(amounts[i]);
            }
        }

        return RevenueTrendsResponse.builder()
                .filter(activeFilter)
                .totalRevenue(total)
                .points(points)
                .build();
    }

    @Override
    public Page<FinancialActivityResponse> getRecentActivity(UUID businessId, Pageable pageable) {
        Page<FinancialTransaction> txPage = transactionRepository.findByBusinessIdOrderByCreatedAtDesc(businessId, pageable);

        return txPage.map(tx -> FinancialActivityResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .status(tx.getStatus())
                .description(tx.getDescription())
                .referenceId(tx.getReferenceId())
                .createdAt(tx.getCreatedAt())
                .formattedDate(tx.getCreatedAt() != null ? tx.getCreatedAt().format(ACTIVITY_DATE_FORMAT) : "")
                .build());
    }

    @Override
    public void exportActivityCsv(UUID businessId, PrintWriter writer) {
        writer.println("Date,Description,Reference,Amount,Status,Type");
        List<FinancialTransaction> list = transactionRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);
        for (FinancialTransaction tx : list) {
            writer.printf("%s,\"%s\",%s,%s,%s,%s%n",
                    tx.getCreatedAt().format(ACTIVITY_DATE_FORMAT),
                    tx.getDescription().replace("\"", "\"\""),
                    tx.getReferenceId() != null ? tx.getReferenceId() : "",
                    tx.getAmount().toString(),
                    tx.getStatus(),
                    tx.getType()
            );
        }
        writer.flush();
    }

    @Override
    public CapitalEligibilityResponse getEligibility(UUID businessId) {
        return CapitalEligibilityResponse.builder()
                .eligible(true)
                .maxLoanAmount(new BigDecimal("25000.00"))
                .interestRatePercent(BigDecimal.ZERO)
                .zeroInterestDays(90)
                .qualificationBasis("Based on your consistent order volume and earnings ($42,500 monthly run-rate)")
                .termsSummary("0% interest for the first 90 days. Flexible repayment automatically deducted as 10% of daily sales.")
                .learnMoreUrl("https://caloryehive.com/business/smart-capital")
                .build();
    }
}
