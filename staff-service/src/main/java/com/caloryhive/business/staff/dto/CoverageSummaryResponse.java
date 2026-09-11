package com.caloryhive.business.staff.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverageSummaryResponse {

    private double totalScheduledHours;
    private double budgetHours;
    private double utilizationPercentage;
    private List<PeakHourBucket> peakHourCoverage;

    // Step 6 enhanced fields
    private LocalDate startDate;
    private LocalDate endDate;
    private int requiredStaff;
    private int totalPeriods;
    private int coveredPeriods;
    private int partiallyCoveredPeriods;
    private int uncoveredPeriods;
    private List<CoveragePeriodDetail> coveragePeriods;
    private String peakTimeSlot;
    private long peakStaffCount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeakHourBucket {
        private String timeSlot; // "10a", "12p", "2p", "4p", "6p", "8p"
        private int hour;
        private long staffCount;
    }
}
