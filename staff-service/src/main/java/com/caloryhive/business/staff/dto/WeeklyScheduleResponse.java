package com.caloryhive.business.staff.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private double totalScheduledHours;

    @Builder.Default
    private double budgetHours = 160.0;

    @Builder.Default
    private List<StaffWeeklyScheduleResponse> staffSchedules = new ArrayList<>();
}
