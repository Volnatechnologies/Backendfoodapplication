package com.caloryhive.business.staff.service;

import com.caloryhive.business.staff.dto.CreateStaffRequest;
import com.caloryhive.business.staff.dto.StaffMetricsResponse;
import com.caloryhive.business.staff.dto.StaffResponse;
import com.caloryhive.business.staff.dto.UpdateStaffRequest;
import com.caloryhive.business.staff.entity.enums.StaffStatus;

import java.util.List;
import java.util.UUID;

public interface StaffService {

    StaffResponse createStaff(CreateStaffRequest request);

    List<StaffResponse> getAllStaff(StaffStatus status, String role);

    StaffResponse getStaffById(UUID id);

    StaffResponse updateStaff(UUID id, UpdateStaffRequest request);

    void deleteStaff(UUID id);

    List<StaffResponse> searchStaff(String keyword);

    StaffMetricsResponse getMetrics();
}
