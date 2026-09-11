package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.StaffStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStaffRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Role must not be blank")
    @Size(max = 100, message = "Role cannot exceed 100 characters")
    private String role;

    private StaffStatus status;

    @DecimalMin(value = "0.0", message = "Performance must be between 0.0 and 5.0")
    @DecimalMax(value = "5.0", message = "Performance must be between 0.0 and 5.0")
    private BigDecimal performance;

    @Size(max = 10, message = "Avatar initials cannot exceed 10 characters")
    private String avatarInitials;

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Size(max = 30, message = "Phone cannot exceed 30 characters")
    private String phone;

    @Size(max = 100, message = "Station/area cannot exceed 100 characters")
    private String stationArea;

    @DecimalMin(value = "0.0", message = "Hourly rate must be non-negative")
    private BigDecimal hourlyRate;

    @DecimalMin(value = "0.0", message = "Weekly budget hours must be non-negative")
    private BigDecimal weeklyBudgetHours;

    private Boolean active;
}
