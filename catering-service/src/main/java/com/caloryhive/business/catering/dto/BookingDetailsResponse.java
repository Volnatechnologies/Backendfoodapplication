package com.caloryhive.business.catering.dto;

import com.caloryhive.business.common.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsResponse {
    private UUID id;
    private String bookingCode;
    private String eventName;
    private UUID eventTypeId;
    private String eventType;
    private LocalDate date;
    private LocalTime time;
    private Integer guestCount;
    private String venueAddress;
    private String specialInstructions;
    private MenuPackageSummary menuPackage;
    @Builder.Default
    private List<CustomOptionSummary> customOptions = new ArrayList<>();
    private FinancialSummaryResponse financials;
    private PricingSummary pricing;
    private BookingStatus status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuPackageSummary {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal pricePerGuest;
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomOptionSummary {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingSummary {
        private BigDecimal basePrice;
        private BigDecimal subtotal;
        private BigDecimal customOptionsTotal;
        private BigDecimal serviceFee;
        private BigDecimal tax;
        private BigDecimal finalTotal;
        private BigDecimal depositRequired;
    }
}
