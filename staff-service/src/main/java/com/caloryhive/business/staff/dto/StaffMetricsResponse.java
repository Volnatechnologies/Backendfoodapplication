package com.caloryhive.business.staff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffMetricsResponse {
    private long totalHeadcount;
    private long activeToday;
    private long lateAbsent;
}
