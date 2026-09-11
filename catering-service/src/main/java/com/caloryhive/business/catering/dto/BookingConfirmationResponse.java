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
public class BookingConfirmationResponse {
    private UUID bookingId;
    private String bookingCode;
    private String eventName;
    private LocalDate date;
    private LocalTime time;
    private BigDecimal totalValue;
    private BigDecimal depositPaid;
    private String venueAddress;
    private BookingStatus status;
    @Builder.Default
    private List<NextStepInfo> nextSteps = new ArrayList<>();
    @Builder.Default
    private List<String> stepTitles = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextStepInfo {
        private int stepNumber;
        private String title;
        private String description;
        private String icon;
        private boolean completed;
    }
}
