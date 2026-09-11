package com.caloryhive.business.staff.entity;

import com.caloryhive.business.common.AuditEntity;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "staff_members", indexes = {
        @Index(name = "idx_staff_status", columnList = "status"),
        @Index(name = "idx_staff_role", columnList = "role"),
        @Index(name = "idx_staff_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Staff extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Staff name is required")
    @Size(max = 100, message = "Staff name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Staff role is required")
    @Size(max = 100, message = "Staff role cannot exceed 100 characters")
    @Column(name = "role", nullable = false, length = 100)
    private String role;

    @NotNull(message = "Staff status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StaffStatus status = StaffStatus.OFF_DUTY;

    @DecimalMin(value = "0.0", message = "Performance rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Performance rating cannot exceed 5.0")
    @Column(name = "performance", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal performance = BigDecimal.valueOf(5.0);

    @Size(max = 10)
    @Column(name = "avatar_initials", length = 10)
    private String avatarInitials;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Size(max = 100)
    @Column(name = "station_area", length = 100)
    private String stationArea;

    @DecimalMin(value = "0.0", message = "Hourly rate must be non-negative")
    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @DecimalMin(value = "0.0", message = "Weekly budget hours must be non-negative")
    @Column(name = "weekly_budget_hours", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal weeklyBudgetHours = BigDecimal.valueOf(40.0);

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Shift> shifts = new ArrayList<>();

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<ShiftRequest> requests = new ArrayList<>();

    public void generateAvatarInitials() {
        if (this.name != null && !this.name.trim().isEmpty()) {
            String[] parts = this.name.trim().split("\\s+");
            if (parts.length == 1) {
                this.avatarInitials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                this.avatarInitials = ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
            }
        }
    }
}
