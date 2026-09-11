package com.caloryhive.business.catering.service;

import com.caloryhive.business.catering.dto.FinancialSummaryResponse;
import com.caloryhive.business.catering.entity.CateringBooking;
import com.caloryhive.business.catering.entity.CateringCustomOption;
import com.caloryhive.business.catering.entity.CateringMenuPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;

@Service
public class PricingService {

    @Value("${app.pricing.service-fee-percent:0.10}")
    private BigDecimal serviceFeePercent = new BigDecimal("0.10");

    @Value("${app.pricing.tax-percent:0.0825}")
    private BigDecimal taxPercent = new BigDecimal("0.0825");

    @Value("${app.pricing.deposit-percent:0.30}")
    private BigDecimal depositPercent = new BigDecimal("0.30");

    public PricingBreakdown calculatePricing(CateringMenuPackage menuPackage, Collection<CateringCustomOption> options,
                                            Integer guestCount) {
        if (guestCount == null || guestCount <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than 0");
        }

        BigDecimal pricePerGuest = menuPackage != null && menuPackage.getPricePerGuest() != null
                ? menuPackage.getPricePerGuest()
                : BigDecimal.ZERO;

        if (pricePerGuest.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Package price cannot be negative");
        }

        BigDecimal basePrice = pricePerGuest.multiply(BigDecimal.valueOf(guestCount.longValue()))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal customOptionsTotal = BigDecimal.ZERO;
        if (options != null) {
            for (CateringCustomOption opt : options) {
                if (opt != null && opt.getPrice() != null && opt.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Custom option price cannot be negative: " + opt.getName());
                }
            }
            customOptionsTotal = options.stream()
                    .filter(Objects::nonNull)
                    .map(opt -> opt.getPrice() != null ? opt.getPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal subtotal = basePrice.add(customOptionsTotal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceFee = subtotal.multiply(serviceFeePercent).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxableAmount = subtotal.add(serviceFee);
        BigDecimal tax = taxableAmount.multiply(taxPercent).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.add(serviceFee).add(tax).setScale(2, RoundingMode.HALF_UP);
        BigDecimal depositRequired = finalTotal.multiply(depositPercent).setScale(2, RoundingMode.HALF_UP);

        return new PricingBreakdown(basePrice, customOptionsTotal, subtotal, serviceFee, tax, finalTotal, depositRequired);
    }

    public void applyPricing(CateringBooking booking, CateringMenuPackage menuPackage,
                            Collection<CateringCustomOption> options) {
        PricingBreakdown breakdown = calculatePricing(menuPackage, options, booking.getGuestCount());
        booking.setBasePrice(breakdown.basePrice());
        booking.setCustomOptionsTotal(breakdown.customOptionsTotal());
        booking.setSubtotal(breakdown.subtotal());
        booking.setServiceFee(breakdown.serviceFee());
        booking.setTax(breakdown.tax());
        booking.setFinalTotal(breakdown.finalTotal());
        booking.setDepositPercentage(depositPercent);
        booking.setDepositRequired(breakdown.depositRequired());
    }

    public FinancialSummaryResponse toFinancialSummary(PricingBreakdown breakdown, Integer guestCount) {
        return FinancialSummaryResponse.builder()
                .guestCount(guestCount)
                .basePrice(breakdown.basePrice())
                .customOptionsTotal(breakdown.customOptionsTotal())
                .subtotal(breakdown.subtotal())
                .serviceFeePercentage(serviceFeePercent)
                .serviceFee(breakdown.serviceFee())
                .taxPercentage(taxPercent)
                .tax(breakdown.tax())
                .finalTotal(breakdown.finalTotal())
                .depositPercentage(depositPercent)
                .depositRequired(breakdown.depositRequired())
                .build();
    }

    public record PricingBreakdown(
            BigDecimal basePrice,
            BigDecimal customOptionsTotal,
            BigDecimal subtotal,
            BigDecimal serviceFee,
            BigDecimal tax,
            BigDecimal finalTotal,
            BigDecimal depositRequired) {
    }
}
