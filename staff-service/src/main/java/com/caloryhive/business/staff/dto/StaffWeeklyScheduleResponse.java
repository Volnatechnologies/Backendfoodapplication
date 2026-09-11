package com.caloryhive.business.staff.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffWeeklyScheduleResponse {

    private UUID staffId;
    private String staffName;
    private String staffRole;
    private String avatarInitials;
    private double totalHours;

    @Builder.Default
    private List<ShiftResponse> shifts = new ArrayList<>();
}
