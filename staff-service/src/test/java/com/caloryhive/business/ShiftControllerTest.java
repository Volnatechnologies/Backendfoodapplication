package com.caloryhive.business;

import com.caloryhive.business.staff.dto.CreateShiftRequest;
import com.caloryhive.business.staff.dto.UpdateShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.ShiftCategory;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
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
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StaffRepository staffRepository;

    private Staff jane;
    private Staff alex;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
        staffRepository.deleteAll();

        jane = staffRepository.save(Staff.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .status(StaffStatus.CLOCKED_IN)
                .performance(BigDecimal.valueOf(4.90))
                .email("jane.smith@caloryhive.com")
                .avatarInitials("JS")
                .active(true)
                .build());

        alex = staffRepository.save(Staff.builder()
                .name("Alex Reed")
                .role("Sous Chef")
                .status(StaffStatus.OFF_DUTY)
                .performance(BigDecimal.valueOf(4.70))
                .email("alex.reed@caloryhive.com")
                .avatarInitials("AR")
                .active(true)
                .build());
    }

    @Test
    @DisplayName("POST /api/shifts - Successfully creates a shift")
    void testCreateShift_Success() throws Exception {
        CreateShiftRequest request = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .role("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .notes("Opening kitchen prep")
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.staffId", is(jane.getId().toString())))
                .andExpect(jsonPath("$.staffName", is("Jane Smith")))
                .andExpect(jsonPath("$.role", is("Head Chef")))
                .andExpect(jsonPath("$.stationArea", is("Prep")))
                .andExpect(jsonPath("$.shiftType", is("REGULAR")))
                .andExpect(jsonPath("$.shiftCategory", is("MORNING")))
                .andExpect(jsonPath("$.durationHours", is(8.0)));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects non-existent staff member (404)")
    void testCreateShift_InvalidStaffId() throws Exception {
        CreateShiftRequest request = CreateShiftRequest.builder()
                .staffId(UUID.randomUUID())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .role("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects start time after or equal to end time (overnight/invalid times)")
    void testCreateShift_InvalidTimes() throws Exception {
        CreateShiftRequest request = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(8, 0)) // Overnight
                .role("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Start time")));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects shift duration under 30 minutes")
    void testCreateShift_TooShortDuration() throws Exception {
        CreateShiftRequest request = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 15)) // 15 mins
                .role("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("at least 30 minutes")));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects overlapping shifts for the same staff member")
    void testCreateShift_OverlappingShifts() throws Exception {
        CreateShiftRequest request1 = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .role("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Overlapping: 10:00 to 14:00 (inside 08:00 - 16:00)
        CreateShiftRequest request2 = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(14, 0))
                .role("Head Chef")
                .stationArea("Service")
                .shiftType(ShiftType.OVERTIME)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("DUPLICATE_RESOURCE")))
                .andExpect(jsonPath("$.message", containsString("overlaps with existing shift")));

        // Adjacent shift before (06:00 - 08:00) should SUCCEED
        CreateShiftRequest adjacentBefore = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(8, 0))
                .role("Prep Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adjacentBefore)))
                .andExpect(status().isCreated());

        // Adjacent shift after (16:00 - 20:00) should SUCCEED
        CreateShiftRequest adjacentAfter = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(20, 0))
                .role("Closing Chef")
                .stationArea("Closing")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adjacentAfter)))
                .andExpect(status().isCreated());

        // Different staff member at same time should SUCCEED
        CreateShiftRequest requestAlex = CreateShiftRequest.builder()
                .staffId(alex.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .role("Sous Chef")
                .stationArea("Line")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAlex)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /api/shifts - Test date filtering, staff filtering, and date range filtering")
    void testGetShifts_Filtering() throws Exception {
        // Shift 1: Jane on Mon Oct 23
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(LocalDate.of(2023, 10, 23))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Head Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // Shift 2: Jane on Tue Oct 24
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(LocalDate.of(2023, 10, 24))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Head Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // Shift 3: Alex on Wed Oct 25
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(alex.getId())
                        .date(LocalDate.of(2023, 10, 25))
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(18, 0))
                        .role("Sous Chef")
                        .stationArea("Line")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // Filter by exact date: 2023-10-23 -> 1 shift
        mockMvc.perform(get("/api/shifts").param("date", "2023-10-23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffName", is("Jane Smith")));

        // Filter by staffId: Alex -> 1 shift
        mockMvc.perform(get("/api/shifts").param("staffId", alex.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffName", is("Alex Reed")));

        // Filter by date range: Oct 23 - Oct 24 -> 2 shifts (Jane's shifts)
        mockMvc.perform(get("/api/shifts")
                        .param("startDate", "2023-10-23")
                        .param("endDate", "2023-10-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("PUT & DELETE /api/shifts/{id} - Update, retrieve, and delete shifts")
    void testUpdateAndDeleteShift() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                                .staffId(jane.getId())
                                .date(LocalDate.of(2023, 10, 25))
                                .startTime(LocalTime.of(12, 0))
                                .endTime(LocalTime.of(20, 0))
                                .role("Head Chef")
                                .stationArea("Service")
                                .shiftType(ShiftType.REGULAR)
                                .notes("Original notes")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();

        String idStr = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Get by ID
        mockMvc.perform(get("/api/shifts/" + idStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationArea", is("Service")))
                .andExpect(jsonPath("$.shiftCategory", is("AFTERNOON")));

        // Update shift
        UpdateShiftRequest updateRequest = UpdateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 25))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(21, 0))
                .role("Head Chef")
                .stationArea("Closing")
                .shiftType(ShiftType.OVERTIME)
                .status(ShiftStatus.PUBLISHED)
                .notes("Updated closing instructions")
                .build();

        mockMvc.perform(put("/api/shifts/" + idStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationArea", is("Closing")))
                .andExpect(jsonPath("$.startTime", is("13:00")))
                .andExpect(jsonPath("$.endTime", is("21:00")))
                .andExpect(jsonPath("$.status", is("PUBLISHED")));

        // Delete shift
        mockMvc.perform(delete("/api/shifts/" + idStr))
                .andExpect(status().isNoContent());

        // Delete again -> 404
        mockMvc.perform(delete("/api/shifts/" + idStr))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/shifts/weekly - Weekly schedule aggregated by staff member")
    void testGetWeeklySchedule() throws Exception {
        LocalDate monday = LocalDate.of(2023, 10, 23);

        // Jane: Mon 08:00 - 16:00 (8h)
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(monday)
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Head Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        // Alex: Wed 10:00 - 18:00 (8h)
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(alex.getId())
                        .date(monday.plusDays(2))
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(18, 0))
                        .role("Sous Chef")
                        .stationArea("Line")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/shifts/weekly").param("startDate", "2023-10-23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate", is("2023-10-23")))
                .andExpect(jsonPath("$.endDate", is("2023-10-29")))
                .andExpect(jsonPath("$.totalScheduledHours", is(16.0)))
                .andExpect(jsonPath("$.budgetHours", is(160.0)))
                .andExpect(jsonPath("$.staffSchedules", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/shifts/coverage-summary - Calculate total scheduled hours, budget, and peak hour coverage")
    void testGetCoverageSummary() throws Exception {
        LocalDate today = LocalDate.now();

        // 10:00 - 18:00 on today
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(today)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(18, 0))
                        .role("Head Chef")
                        .stationArea("Kitchen")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/shifts/coverage-summary").param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScheduledHours", is(8.0)))
                .andExpect(jsonPath("$.budgetHours", is(160.0)))
                .andExpect(jsonPath("$.peakHourCoverage", hasSize(6)))
                .andExpect(jsonPath("$.peakHourCoverage[1].timeSlot", is("12p")))
                .andExpect(jsonPath("$.peakHourCoverage[1].staffCount", is(1)));
    }

    @Test
    @DisplayName("POST /api/shifts/publish - Batch publish draft shifts")
    void testPublishShifts() throws Exception {
        LocalDate monday = LocalDate.of(2023, 10, 23);

        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(monday)
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Head Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .status(ShiftStatus.DRAFT)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(post("/api/shifts/publish")
                        .param("startDate", "2023-10-23")
                        .param("endDate", "2023-10-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PUBLISHED")));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects blank role with 400 Bad Request")
    void testCreateShift_BlankRole() throws Exception {
        CreateShiftRequest request = CreateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .role("   ") // blank
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .build();

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")));
    }

    @Test
    @DisplayName("POST /api/shifts - Rejects invalid shift type with 400 Bad Request")
    void testCreateShift_InvalidShiftType() throws Exception {
        String invalidJson = String.format(
                "{\"staffId\":\"%s\",\"date\":\"2023-10-23\",\"startTime\":\"08:00\",\"endTime\":\"16:00\",\"role\":\"Chef\",\"stationArea\":\"Kitchen\",\"shiftType\":\"INVALID_TYPE\"}",
                jane.getId());

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("INVALID_REQUEST_BODY")));
    }

    @Test
    @DisplayName("GET /api/shifts/{id} - Rejects non-existent shift ID with 404")
    void testGetShiftById_NotFound() throws Exception {
        mockMvc.perform(get("/api/shifts/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /api/shifts - Filter returns empty array when no matches")
    void testGetShifts_EmptyResults() throws Exception {
        mockMvc.perform(get("/api/shifts").param("date", "2099-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/shifts - Get all shifts without filters")
    void testGetAllShifts_NoFilter() throws Exception {
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                        .staffId(jane.getId())
                        .date(LocalDate.of(2023, 10, 23))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(16, 0))
                        .role("Head Chef")
                        .stationArea("Prep")
                        .shiftType(ShiftType.REGULAR)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dayOfWeek", is("MONDAY")));
    }

    @Test
    @DisplayName("PUT /api/shifts/{id} - Rejects updating non-existent shift with 404")
    void testUpdateShift_NotFound() throws Exception {
        UpdateShiftRequest updateRequest = UpdateShiftRequest.builder()
                .staffId(jane.getId())
                .date(LocalDate.of(2023, 10, 25))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(21, 0))
                .role("Head Chef")
                .stationArea("Closing")
                .shiftType(ShiftType.OVERTIME)
                .build();

        mockMvc.perform(put("/api/shifts/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("DELETE /api/shifts/{id} - Rejects deleting non-existent shift with 404")
    void testDeleteShift_NotFound() throws Exception {
        mockMvc.perform(delete("/api/shifts/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("Shift Entity - Verifies full database persistence and retrieval")
    void testShift_DatabasePersistence() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CreateShiftRequest.builder()
                                .staffId(jane.getId())
                                .date(LocalDate.of(2026, 9, 15))
                                .startTime(LocalTime.of(8, 0))
                                .endTime(LocalTime.of(16, 0))
                                .role("Head Chef")
                                .stationArea("Kitchen")
                                .shiftType(ShiftType.REGULAR)
                                .notes("Morning shift")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();

        String idStr = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        UUID shiftId = UUID.fromString(idStr);

        // Verify entity persisted in repository
        org.junit.jupiter.api.Assertions.assertTrue(shiftRepository.findById(shiftId).isPresent());
        com.caloryhive.business.staff.entity.Shift persisted = shiftRepository.findById(shiftId).get();
        org.junit.jupiter.api.Assertions.assertEquals("Head Chef", persisted.getShiftRole());
        org.junit.jupiter.api.Assertions.assertEquals("Kitchen", persisted.getStationArea());
        org.junit.jupiter.api.Assertions.assertEquals(LocalDate.of(2026, 9, 15), persisted.getShiftDate());
        org.junit.jupiter.api.Assertions.assertEquals(jane.getId(), persisted.getStaff().getId());
    }
}
