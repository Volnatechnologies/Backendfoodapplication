package com.caloryhive.business.staff.dto;

import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRequestResponse {

    private UUID id;

    private UUID staffId;
    private String staffName;
    private String staffRole;
    private String staffAvatarInitials;

    private RequestType requestType;

    private UUID shiftId;
    private String shiftSummary;

    private UUID targetStaffId;
    private String targetStaffName;
    private String targetStaffAvatarInitials;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String reason;
    private String requestDescription;
    private RequestStatus status;
    private String adminComment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
