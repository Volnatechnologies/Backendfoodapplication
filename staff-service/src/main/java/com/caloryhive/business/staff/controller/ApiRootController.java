package com.caloryhive.business.staff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiRootController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiRoot() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "Staff Management and Shift Planning Backend");
        response.put("version", "1.0.0");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("staff", "/api/staff");
        endpoints.put("staffMetrics", "/api/staff/metrics");
        endpoints.put("staffSearch", "/api/staff/search?keyword={keyword}");
        endpoints.put("shifts", "/api/shifts");
        endpoints.put("weeklySchedule", "/api/shifts/weekly?startDate={YYYY-MM-DD}");
        endpoints.put("coverageSummary", "/api/shifts/coverage-summary?date={YYYY-MM-DD}&startDate={YYYY-MM-DD}&endDate={YYYY-MM-DD}&requiredStaff={N}");
        endpoints.put("peakHourAnalysis", "/api/shifts/peak-hours?date={YYYY-MM-DD}&startDate={YYYY-MM-DD}&endDate={YYYY-MM-DD}&requiredStaff={N}");
        endpoints.put("publishShifts", "/api/shifts/publish?startDate={YYYY-MM-DD}&endDate={YYYY-MM-DD}");
        endpoints.put("shiftRequests", "/api/shift-requests");

        response.put("endpoints", endpoints);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
