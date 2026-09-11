package com.caloryhive.business.staff.mapper;

import com.caloryhive.business.staff.dto.CreateShiftRequest;
import com.caloryhive.business.staff.dto.ShiftResponse;
import com.caloryhive.business.staff.dto.UpdateShiftRequest;
import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.ShiftCategory;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.entity.enums.ShiftType;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class ShiftMapper {

    public ShiftResponse toResponse(Shift shift) {
        if (shift == null) {
            return null;
        }

        Staff staff = shift.getStaff();

        return ShiftResponse.builder()
                .id(shift.getId())
                .staffId(staff != null ? staff.getId() : null)
                .staffName(staff != null ? staff.getName() : null)
                .staffRole(staff != null ? staff.getRole() : null)
                .staffAvatarInitials(staff != null ? staff.getAvatarInitials() : null)
                .date(shift.getShiftDate())
                .shiftDate(shift.getShiftDate())
                .dayOfWeek(shift.getShiftDate() != null ? shift.getShiftDate().getDayOfWeek().name() : null)
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .role(shift.getShiftRole())
                .shiftRole(shift.getShiftRole())
                .stationArea(shift.getStationArea())
                .shiftType(shift.getShiftType())
                .shiftCategory(shift.getShiftCategory())
                .status(shift.getStatus())
                .durationHours(shift.getDurationInHours())
                .notes(shift.getNotes())
                .createdAt(shift.getCreatedAt())
                .updatedAt(shift.getUpdatedAt())
                .build();
    }

    public Shift toEntity(CreateShiftRequest request, Staff staff) {
        if (request == null) {
            return null;
        }

        ShiftCategory category = request.getShiftCategory();
        if (category == null && request.getStartTime() != null) {
            category = determineCategory(request.getStartTime());
        }

        ShiftStatus status = request.getStatus() != null ? request.getStatus() : ShiftStatus.DRAFT;
        ShiftType shiftType = request.getShiftType() != null ? request.getShiftType() : ShiftType.REGULAR;

        return Shift.builder()
                .staff(staff)
                .shiftDate(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .shiftRole(request.getRole() != null ? request.getRole().trim() : (staff != null ? staff.getRole() : ""))
                .stationArea(request.getStationArea() != null ? request.getStationArea().trim() : "")
                .shiftType(shiftType)
                .shiftCategory(category)
                .status(status)
                .notes(request.getNotes())
                .build();
    }

    public void updateEntity(Shift shift, UpdateShiftRequest request, Staff staff) {
        if (shift == null || request == null) {
            return;
        }

        if (staff != null) {
            shift.setStaff(staff);
        }

        shift.setShiftDate(request.getDate());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setShiftRole(request.getRole().trim());
        shift.setStationArea(request.getStationArea().trim());

        if (request.getShiftType() != null) {
            shift.setShiftType(request.getShiftType());
        }

        if (request.getShiftCategory() != null) {
            shift.setShiftCategory(request.getShiftCategory());
        } else if (request.getStartTime() != null) {
            shift.setShiftCategory(determineCategory(request.getStartTime()));
        }

        if (request.getStatus() != null) {
            shift.setStatus(request.getStatus());
        }

        shift.setNotes(request.getNotes());
    }

    private ShiftCategory determineCategory(LocalTime startTime) {
        int hour = startTime.getHour();
        if (hour < 12) {
            return ShiftCategory.MORNING;
        } else if (hour < 17) {
            return ShiftCategory.AFTERNOON;
        } else {
            return ShiftCategory.EVENING;
        }
    }
}
