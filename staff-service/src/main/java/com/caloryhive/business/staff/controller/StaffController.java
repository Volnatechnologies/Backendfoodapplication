package com.caloryhive.business.staff.controller;

import com.caloryhive.business.staff.dto.CreateStaffRequest;
import com.caloryhive.business.staff.dto.StaffMetricsResponse;
import com.caloryhive.business.staff.dto.StaffResponse;
import com.caloryhive.business.staff.dto.UpdateStaffRequest;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.caloryhive.business.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        log.info("REST request to create Staff: {}", request.getName());
        StaffResponse response = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff(
            @RequestParam(required = false) StaffStatus status,
            @RequestParam(required = false) String role) {
        log.info("REST request to get all Staff with status: {}, role: {}", status, role);
        List<StaffResponse> response = staffService.getAllStaff(status, role);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics")
    public ResponseEntity<StaffMetricsResponse> getMetrics() {
        log.info("REST request to get Staff KPIs (Headcount, Active Today, Late/Absent)");
        StaffMetricsResponse response = staffService.getMetrics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable UUID id) {
        log.info("REST request to get Staff by id: {}", id);
        StaffResponse response = staffService.getStaffById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffRequest request) {
        log.info("REST request to update Staff id: {}", id);
        StaffResponse response = staffService.updateStaff(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        log.info("REST request to delete Staff id: {}", id);
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<StaffResponse>> searchStaff(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        log.info("REST request to search Staff with keyword: '{}'", keyword);
        List<StaffResponse> response = staffService.searchStaff(keyword);
        return ResponseEntity.ok(response);
    }
}
