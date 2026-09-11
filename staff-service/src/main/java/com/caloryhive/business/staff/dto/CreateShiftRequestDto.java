package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShiftRequestDto {

    @NotNull(message = "Staff ID is required")
    private UUID staffId;

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    private UUID shiftId; // Required for SHIFT_SWAP

    private UUID targetStaffId; // Required for SHIFT_SWAP

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
