package com.caloryhive.business.staff.service.impl;

import com.caloryhive.business.common.exception.DuplicateResourceException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.staff.dto.CreateStaffRequest;
import com.caloryhive.business.staff.dto.StaffMetricsResponse;
import com.caloryhive.business.staff.dto.StaffResponse;
import com.caloryhive.business.staff.dto.UpdateStaffRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.caloryhive.business.staff.mapper.StaffMapper;
import com.caloryhive.business.staff.repository.StaffRepository;
import com.caloryhive.business.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        log.info("Creating new staff member: {}", request.getName());

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim().toLowerCase();
            if (staffRepository.existsByEmail(email)) {
                throw new DuplicateResourceException("Staff with email '" + email + "' already exists");
            }
        }

        Staff staff = staffMapper.toEntity(request);
        Staff savedStaff = staffRepository.save(staff);

        log.info("Successfully created staff member with id: {}", savedStaff.getId());
        return staffMapper.toResponse(savedStaff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff(StaffStatus status, String role) {
        log.info("Fetching staff list with filters - status: {}, role: {}", status, role);

        List<Staff> staffList;
        boolean hasRole = role != null && !role.trim().isEmpty();

        if (status != null && hasRole) {
            staffList = staffRepository.findByStatusAndRoleIgnoreCaseAndActiveTrue(status, role.trim());
        } else if (status != null) {
            staffList = staffRepository.findByStatusAndActiveTrue(status);
        } else if (hasRole) {
            staffList = staffRepository.findByRoleIgnoreCaseAndActiveTrue(role.trim());
        } else {
            staffList = staffRepository.findByActiveTrueOrderByNameAsc();
        }

        return staffList.stream()
                .map(staffMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffById(UUID id) {
        log.info("Fetching staff details for id: {}", id);

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));

        return staffMapper.toResponse(staff);
    }

    @Override
    public StaffResponse updateStaff(UUID id, UpdateStaffRequest request) {
        log.info("Updating staff member with id: {}", id);

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim().toLowerCase();
            if (staffRepository.existsByEmailAndIdNot(email, id)) {
                throw new DuplicateResourceException("Staff with email '" + email + "' already exists");
            }
        }

        staffMapper.updateEntity(staff, request);
        Staff updatedStaff = staffRepository.save(staff);

        log.info("Successfully updated staff member with id: {}", updatedStaff.getId());
        return staffMapper.toResponse(updatedStaff);
    }

    @Override
    public void deleteStaff(UUID id) {
        log.info("Deleting staff member with id: {}", id);

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));

        staffRepository.delete(staff);
        log.info("Successfully deleted staff member with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> searchStaff(String keyword) {
        log.info("Searching staff with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStaff(null, null);
        }

        List<Staff> staffList = staffRepository.searchByKeyword(keyword.trim());
        return staffList.stream()
                .map(staffMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StaffMetricsResponse getMetrics() {
        log.info("Calculating staff metrics for dashboard cards");

        long total = staffRepository.countByActiveTrue();
        long active = staffRepository.countByStatusInAndActiveTrue(List.of(StaffStatus.ACTIVE, StaffStatus.CLOCKED_IN));
        long lateAbsent = staffRepository.countByStatusInAndActiveTrue(List.of(StaffStatus.LATE, StaffStatus.ABSENT));

        return StaffMetricsResponse.builder()
                .totalHeadcount(total)
                .activeToday(active)
                .lateAbsent(lateAbsent)
                .build();
    }
}
