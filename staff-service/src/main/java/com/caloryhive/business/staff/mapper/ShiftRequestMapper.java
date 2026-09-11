package com.caloryhive.business.staff.mapper;

import com.caloryhive.business.staff.dto.ShiftRequestResponse;
import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.ShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import org.springframework.stereotype.Component;

@Component
public class ShiftRequestMapper {

    public ShiftRequestResponse toResponse(ShiftRequest request) {
        if (request == null) {
            return null;
        }

        Staff staff = request.getStaff();
        Staff target = request.getTargetStaff();
        Shift shift = request.getShift();

        String shiftSummary = null;
        if (shift != null) {
            shiftSummary = String.format("%s, %s - %s (%s)",
                    shift.getShiftDate(),
                    shift.getStartTime(),
                    shift.getEndTime(),
                    shift.getStationArea());
        }

        String description = request.getReason();
        if (request.getRequestType() == com.caloryhive.business.staff.entity.enums.RequestType.SHIFT_SWAP) {
            String targetName = target != null ? target.getName() : "colleague";
            description = (request.getReason() != null ? request.getReason() : "Swap shift with " + targetName);
        } else if (description == null && request.getRequestedDate() != null) {
            description = "Time off on " + request.getRequestedDate();
        }

        return ShiftRequestResponse.builder()
                .id(request.getId())
                .staffId(staff != null ? staff.getId() : null)
                .staffName(staff != null ? staff.getName() : null)
                .staffRole(staff != null ? staff.getRole() : null)
                .staffAvatarInitials(staff != null ? staff.getAvatarInitials() : null)
                .requestType(request.getRequestType())
                .shiftId(shift != null ? shift.getId() : null)
                .shiftSummary(shiftSummary)
                .targetStaffId(target != null ? target.getId() : null)
                .targetStaffName(target != null ? target.getName() : null)
                .targetStaffAvatarInitials(target != null ? target.getAvatarInitials() : null)
                .requestedDate(request.getRequestedDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .requestDescription(description)
                .status(request.getStatus())
                .adminComment(request.getAdminComment())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
