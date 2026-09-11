package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private UUID id;
    private String name;
    private String role;
    private StaffStatus status;
    private BigDecimal performance;
    private String avatarInitials;
    private String email;
    private String phone;
    private String stationArea;
    private BigDecimal hourlyRate;
    private BigDecimal weeklyBudgetHours;
    private Boolean active;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
