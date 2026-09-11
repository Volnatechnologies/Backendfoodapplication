package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.CoverageStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveragePeriodDetail {

    private LocalDate date;
    private String timeSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private int requiredStaff;
    private long scheduledStaff;
    private CoverageStatus coverageStatus;
    private int shortage;
    private int excess;
    private boolean isPeak;
}
