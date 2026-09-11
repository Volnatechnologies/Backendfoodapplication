package com.caloryhive.business.staff.mapper;

import com.caloryhive.business.staff.dto.CreateStaffRequest;
import com.caloryhive.business.staff.dto.StaffResponse;
import com.caloryhive.business.staff.dto.UpdateStaffRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StaffMapper {

    public StaffResponse toResponse(Staff staff) {
        if (staff == null) {
            return null;
        }

        return StaffResponse.builder()
                .id(staff.getId())
                .name(staff.getName())
                .role(staff.getRole())
                .status(staff.getStatus())
                .performance(staff.getPerformance())
                .avatarInitials(staff.getAvatarInitials())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .stationArea(staff.getStationArea())
                .hourlyRate(staff.getHourlyRate())
                .weeklyBudgetHours(staff.getWeeklyBudgetHours())
                .active(staff.getActive())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }

    public Staff toEntity(CreateStaffRequest request) {
        if (request == null) {
            return null;
        }

        Staff staff = Staff.builder()
                .name(request.getName().trim())
                .role(request.getRole().trim())
                .status(request.getStatus() != null ? request.getStatus() : StaffStatus.OFF_DUTY)
                .performance(request.getPerformance() != null ? request.getPerformance() : BigDecimal.valueOf(5.0))
                .avatarInitials(request.getAvatarInitials())
                .email(request.getEmail() != null && !request.getEmail().trim().isEmpty() ? request.getEmail().trim().toLowerCase() : null)
                .phone(request.getPhone())
                .stationArea(request.getStationArea())
                .hourlyRate(request.getHourlyRate())
                .weeklyBudgetHours(request.getWeeklyBudgetHours() != null ? request.getWeeklyBudgetHours() : BigDecimal.valueOf(40.0))
                .active(true)
                .build();

        if (staff.getAvatarInitials() == null || staff.getAvatarInitials().trim().isEmpty()) {
            staff.generateAvatarInitials();
        }

        return staff;
    }

    public void updateEntity(Staff staff, UpdateStaffRequest request) {
        if (staff == null || request == null) {
            return;
        }

        staff.setName(request.getName().trim());
        staff.setRole(request.getRole().trim());

        if (request.getStatus() != null) {
            staff.setStatus(request.getStatus());
        }

        if (request.getPerformance() != null) {
            staff.setPerformance(request.getPerformance());
        }

        if (request.getAvatarInitials() != null && !request.getAvatarInitials().trim().isEmpty()) {
            staff.setAvatarInitials(request.getAvatarInitials().trim().toUpperCase());
        } else {
            staff.generateAvatarInitials();
        }

        if (request.getEmail() != null) {
            staff.setEmail(!request.getEmail().trim().isEmpty() ? request.getEmail().trim().toLowerCase() : null);
        }

        if (request.getPhone() != null) {
            staff.setPhone(request.getPhone().trim());
        }

        if (request.getStationArea() != null) {
            staff.setStationArea(request.getStationArea().trim());
        }

        if (request.getHourlyRate() != null) {
            staff.setHourlyRate(request.getHourlyRate());
        }

        if (request.getWeeklyBudgetHours() != null) {
            staff.setWeeklyBudgetHours(request.getWeeklyBudgetHours());
        }

        if (request.getActive() != null) {
            staff.setActive(request.getActive());
        }
    }
}
