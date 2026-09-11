package com.caloryhive.business.staff.entity;

import com.caloryhive.business.common.AuditEntity;
import com.caloryhive.business.staff.entity.enums.ShiftCategory;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.entity.enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "shifts", indexes = {
        @Index(name = "idx_shifts_staff_id", columnList = "staff_id"),
        @Index(name = "idx_shifts_date", columnList = "shift_date"),
        @Index(name = "idx_shifts_staff_date", columnList = "staff_id, shift_date"),
        @Index(name = "idx_shifts_status", columnList = "status"),
        @Index(name = "idx_shifts_category", columnList = "shift_category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Shift extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Staff member is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnoreProperties({"shifts", "requests", "hibernateLazyInitializer", "handler"})
    private Staff staff;

    @NotNull(message = "Shift date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank(message = "Shift role is required")
    @Size(max = 100, message = "Shift role cannot exceed 100 characters")
    @Column(name = "shift_role", nullable = false, length = 100)
    private String shiftRole;

    @NotBlank(message = "Station/area is required")
    @Size(max = 100, message = "Station/area cannot exceed 100 characters")
    @Column(name = "station_area", nullable = false, length = 100)
    private String stationArea;

    @NotNull(message = "Shift type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false, length = 30)
    @Builder.Default
    private ShiftType shiftType = ShiftType.REGULAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_category", length = 30)
    private ShiftCategory shiftCategory;

    @NotNull(message = "Shift status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.DRAFT;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    public void calculateCategoryFromTime() {
        if (this.shiftCategory == null && this.startTime != null) {
            int hour = this.startTime.getHour();
            if (hour < 12) {
                this.shiftCategory = ShiftCategory.MORNING;
            } else if (hour < 17) {
                this.shiftCategory = ShiftCategory.AFTERNOON;
            } else {
                this.shiftCategory = ShiftCategory.EVENING;
            }
        }
    }

    public double getDurationInHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        long minutes;
        if (endTime.isAfter(startTime)) {
            minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        } else {
            // Over midnight shift
            minutes = java.time.Duration.between(startTime, LocalTime.MAX).toMinutes() + 1
                    + java.time.Duration.between(LocalTime.MIN, endTime).toMinutes();
        }
        return minutes / 60.0;
    }
}
