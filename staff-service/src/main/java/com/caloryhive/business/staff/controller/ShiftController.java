package com.caloryhive.business.staff.controller;

import com.caloryhive.business.staff.dto.*;
import com.caloryhive.business.staff.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody CreateShiftRequest request) {
        log.info("REST request to create Shift for staff: {}, date: {}", request.getStaffId(), request.getDate());
        ShiftResponse response = shiftService.createShift(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponse>> getShifts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get Shifts - date: {}, staffId: {}, startDate: {}, endDate: {}", date, staffId, startDate, endDate);
        List<ShiftResponse> response = shiftService.getShifts(date, staffId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponse> getShiftById(@PathVariable UUID id) {
        log.info("REST request to get Shift by id: {}", id);
        ShiftResponse response = shiftService.getShiftById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponse> updateShift(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShiftRequest request) {
        log.info("REST request to update Shift id: {}", id);
        ShiftResponse response = shiftService.updateShift(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable UUID id) {
        log.info("REST request to delete Shift id: {}", id);
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyScheduleResponse> getWeeklySchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        log.info("REST request to get Weekly Schedule starting: {}", startDate);
        WeeklyScheduleResponse response = shiftService.getWeeklySchedule(startDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coverage-summary")
    public ResponseEntity<CoverageSummaryResponse> getCoverageSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer requiredStaff) {
        log.info("REST request to get Coverage Summary - date: {}, startDate: {}, endDate: {}, requiredStaff: {}",
                date, startDate, endDate, requiredStaff);
        CoverageSummaryResponse response = shiftService.getCoverageSummary(date, startDate, endDate, requiredStaff);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/peak-hours")
    public ResponseEntity<PeakHourAnalysisResponse> getPeakHourAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer requiredStaff) {
        log.info("REST request to get Peak Hour Analysis - date: {}, startDate: {}, endDate: {}, requiredStaff: {}",
                date, startDate, endDate, requiredStaff);
        PeakHourAnalysisResponse response = shiftService.getPeakHourAnalysis(date, startDate, endDate, requiredStaff);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/publish")
    public ResponseEntity<List<ShiftResponse>> publishShifts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to publish shifts between {} and {}", startDate, endDate);
        List<ShiftResponse> response = shiftService.publishShifts(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
