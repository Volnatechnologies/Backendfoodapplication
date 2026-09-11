package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.ShiftCategory;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.entity.enums.ShiftType;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateShiftRequest {

    @NotNull(message = "Staff ID is required")
    private UUID staffId;

    @NotNull(message = "Shift date is required")
    @JsonAlias({"shiftDate", "date"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @NotBlank(message = "Shift role is required")
    @Size(max = 100, message = "Shift role cannot exceed 100 characters")
    @JsonAlias({"shiftRole", "role"})
    private String role;

    @NotBlank(message = "Station/area is required")
    @Size(max = 100, message = "Station/area cannot exceed 100 characters")
    private String stationArea;

    @NotNull(message = "Shift type is required")
    private ShiftType shiftType;

    private ShiftCategory shiftCategory;

    private ShiftStatus status;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
