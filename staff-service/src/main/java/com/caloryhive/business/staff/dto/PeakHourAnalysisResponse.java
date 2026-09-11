package com.caloryhive.business.staff.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeakHourAnalysisResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private int defaultRequiredStaff;
    private String peakTimeSlot;
    private long peakStaffCount;
    private int totalPeriodsAnalyzed;
    private int coveredPeriods;
    private int partiallyCoveredPeriods;
    private int uncoveredPeriods;
    private List<CoveragePeriodDetail> periods;
    private List<CoveragePeriodDetail> peakPeriods;
}
