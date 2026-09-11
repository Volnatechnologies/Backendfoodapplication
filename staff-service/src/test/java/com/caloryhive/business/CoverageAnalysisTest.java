package com.caloryhive.business;

import com.caloryhive.business.staff.dto.CreateShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.ShiftType;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.caloryhive.business.staff.repository.ShiftRepository;
import com.caloryhive.business.staff.repository.StaffRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CoverageAnalysisTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StaffRepository staffRepository;

    private Staff chef1;
    private Staff chef2;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
        staffRepository.deleteAll();

        chef1 = staffRepository.save(Staff.builder()
                .name("Chef Gordon")
                .role("Executive Chef")
                .email("gordon@caloryhive.com")
                .phone("1234567890")
                .hourlyRate(BigDecimal.valueOf(35.00))
                .status(StaffStatus.ACTIVE)
                .active(true)
                .build());

        chef2 = staffRepository.save(Staff.builder()
                .name("Chef Marco")
                .role("Sous Chef")
                .email("marco@caloryhive.com")
                .phone("0987654321")
                .hourlyRate(BigDecimal.valueOf(28.00))
                .status(StaffStatus.ACTIVE)
                .active(true)
                .build());
    }

    @Test
    @DisplayName("Coverage Summary - Handles empty data safely without errors")
    void testCoverageSummary_EmptyData() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 1);

        mockMvc.perform(get("/api/shifts/coverage-summary")
                        .param("date", date.toString())
                        .param("requiredStaff", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScheduledHours", is(0.0)))
                .andExpect(jsonPath("$.coveredPeriods", is(0)))
                .andExpect(jsonPath("$.partiallyCoveredPeriods", is(0)))
                .andExpect(jsonPath("$.uncoveredPeriods", is(6)))
                .andExpect(jsonPath("$.totalPeriods", is(6)))
                .andExpect(jsonPath("$.peakStaffCount", is(0)))
                .andExpect(jsonPath("$.coveragePeriods", hasSize(6)))
                .andExpect(jsonPath("$.coveragePeriods[0].coverageStatus", is("UNCOVERED")))
                .andExpect(jsonPath("$.coveragePeriods[0].scheduledStaff", is(0)))
                .andExpect(jsonPath("$.coveragePeriods[0].shortage", is(2)))
                .andExpect(jsonPath("$.coveragePeriods[0].excess", is(0)));
    }

    @Test
    @DisplayName("Peak Hour Analysis - Handles empty data safely")
    void testPeakHourAnalysis_EmptyData() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 1);

        mockMvc.perform(get("/api/shifts/peak-hours")
                        .param("date", date.toString())
                        .param("requiredStaff", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peakStaffCount", is(0)))
                .andExpect(jsonPath("$.peakTimeSlot", is("None")))
                .andExpect(jsonPath("$.totalPeriodsAnalyzed", is(6)))
                .andExpect(jsonPath("$.uncoveredPeriods", is(6)))
                .andExpect(jsonPath("$.peakPeriods", hasSize(0)));
    }

    @Test
    @DisplayName("Coverage Summary - Correctly identifies COVERED, PARTIALLY_COVERED, and UNCOVERED periods")
    void testCoverageSummary_StatusBreakdown() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 10, 5);

        // chef1 works 10:00 to 18:00 (covers 10a, 12p, 2p, 4p)
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(chef1.getId())
                        .date(targetDate)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(18, 0))
                        .role("Executive Chef")
                        .stationArea("Kitchen")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // chef2 works 12:00 to 16:00 (covers 12p, 2p)
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(chef2.getId())
                        .date(targetDate)
                        .startTime(LocalTime.of(12, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Sous Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // With requiredStaff = 2:
        // Slot 0 (10:00-12:00): chef1 only -> scheduledStaff = 1 -> PARTIALLY_COVERED, shortage = 1
        // Slot 1 (12:00-14:00): chef1 & chef2 -> scheduledStaff = 2 -> COVERED, shortage = 0, isPeak = true
        // Slot 2 (14:00-16:00): chef1 & chef2 -> scheduledStaff = 2 -> COVERED, shortage = 0, isPeak = true
        // Slot 3 (16:00-18:00): chef1 only -> scheduledStaff = 1 -> PARTIALLY_COVERED, shortage = 1
        // Slot 4 (18:00-20:00): none -> scheduledStaff = 0 -> UNCOVERED, shortage = 2
        // Slot 5 (20:00-22:00): none -> scheduledStaff = 0 -> UNCOVERED, shortage = 2

        mockMvc.perform(get("/api/shifts/coverage-summary")
                        .param("date", targetDate.toString())
                        .param("requiredStaff", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPeriods", is(6)))
                .andExpect(jsonPath("$.coveredPeriods", is(2)))
                .andExpect(jsonPath("$.partiallyCoveredPeriods", is(2)))
                .andExpect(jsonPath("$.uncoveredPeriods", is(2)))
                .andExpect(jsonPath("$.peakStaffCount", is(2)))
                // Slot 0
                .andExpect(jsonPath("$.coveragePeriods[0].coverageStatus", is("PARTIALLY_COVERED")))
                .andExpect(jsonPath("$.coveragePeriods[0].scheduledStaff", is(1)))
                .andExpect(jsonPath("$.coveragePeriods[0].shortage", is(1)))
                // Slot 1
                .andExpect(jsonPath("$.coveragePeriods[1].coverageStatus", is("COVERED")))
                .andExpect(jsonPath("$.coveragePeriods[1].scheduledStaff", is(2)))
                .andExpect(jsonPath("$.coveragePeriods[1].shortage", is(0)))
                .andExpect(jsonPath("$.coveragePeriods[1].peak", is(true)))
                // Slot 4
                .andExpect(jsonPath("$.coveragePeriods[4].coverageStatus", is("UNCOVERED")))
                .andExpect(jsonPath("$.coveragePeriods[4].scheduledStaff", is(0)))
                .andExpect(jsonPath("$.coveragePeriods[4].shortage", is(2)));
    }

    @Test
    @DisplayName("Peak Hour Analysis - Accurately computes peak hours and excess")
    void testPeakHourAnalysis_Calculation() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 10, 6);

        // chef1 works 10:00 to 14:00
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(chef1.getId())
                        .date(targetDate)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(14, 0))
                        .role("Executive Chef")
                        .stationArea("Kitchen")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // chef2 works 10:00 to 14:00
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(chef2.getId())
                        .date(targetDate)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(14, 0))
                        .role("Sous Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // With requiredStaff = 1:
        // Slot 10:00 - 12:00: scheduledStaff = 2 -> COVERED, excess = 1, isPeak = true
        // Slot 12:00 - 14:00: scheduledStaff = 2 -> COVERED, excess = 1, isPeak = true

        mockMvc.perform(get("/api/shifts/peak-hours")
                        .param("date", targetDate.toString())
                        .param("requiredStaff", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peakStaffCount", is(2)))
                .andExpect(jsonPath("$.coveredPeriods", is(2)))
                .andExpect(jsonPath("$.uncoveredPeriods", is(4)))
                .andExpect(jsonPath("$.peakPeriods", hasSize(2)))
                .andExpect(jsonPath("$.periods[0].excess", is(1)))
                .andExpect(jsonPath("$.periods[0].coverageStatus", is("COVERED")));
    }

    @Test
    @DisplayName("Coverage Summary - Multi-day date range analysis")
    void testCoverageSummary_DateRange() throws Exception {
        LocalDate day1 = LocalDate.of(2026, 10, 10);
        LocalDate day2 = LocalDate.of(2026, 10, 11);

        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(chef1.getId())
                        .date(day1)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(14, 0))
                        .role("Executive Chef")
                        .stationArea("Kitchen")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/shifts/coverage-summary")
                        .param("startDate", day1.toString())
                        .param("endDate", day2.toString())
                        .param("requiredStaff", "1"))
                .andExpect(status().isOk())
                // 2 days * 6 slots = 12 total periods
                .andExpect(jsonPath("$.totalPeriods", is(12)))
                .andExpect(jsonPath("$.coveragePeriods", hasSize(12)))
                .andExpect(jsonPath("$.totalScheduledHours", is(4.0)));
    }

    @Test
    @DisplayName("Coverage Summary - Rejects invalid date range with 400 Bad Request")
    void testCoverageSummary_InvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/shifts/coverage-summary")
                        .param("startDate", "2026-10-15")
                        .param("endDate", "2026-10-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Peak Hour Analysis - Rejects negative required staff with 400 Bad Request")
    void testPeakHourAnalysis_NegativeRequiredStaff() throws Exception {
        mockMvc.perform(get("/api/shifts/peak-hours")
                        .param("requiredStaff", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));
    }
}
