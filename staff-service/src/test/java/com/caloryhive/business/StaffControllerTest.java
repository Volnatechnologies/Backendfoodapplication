package com.caloryhive.business;

import com.caloryhive.business.staff.dto.CreateStaffRequest;
import com.caloryhive.business.staff.dto.UpdateStaffRequest;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StaffRepository staffRepository;

    @BeforeEach
    void setUp() {
        staffRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/staff - Successfully creates staff member")
    void testCreateStaff_Success() throws Exception {
        CreateStaffRequest request = CreateStaffRequest.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .status(StaffStatus.CLOCKED_IN)
                .performance(BigDecimal.valueOf(4.95))
                .email("jane.smith@caloryhive.com")
                .phone("+1-555-0100")
                .stationArea("Kitchen")
                .avatarInitials("JS")
                .hourlyRate(BigDecimal.valueOf(35.00))
                .weeklyBudgetHours(BigDecimal.valueOf(40.00))
                .build();

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.role", is("Head Chef")))
                .andExpect(jsonPath("$.status", is("CLOCKED_IN")))
                .andExpect(jsonPath("$.avatarInitials", is("JS")))
                .andExpect(jsonPath("$.email", is("jane.smith@caloryhive.com")));
    }

    @Test
    @DisplayName("POST /api/staff - Validation error on blank name and blank role")
    void testCreateStaff_ValidationError() throws Exception {
        CreateStaffRequest request = CreateStaffRequest.builder()
                .name("")
                .role(" ")
                .build();

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.fieldErrors.name", notNullValue()))
                .andExpect(jsonPath("$.fieldErrors.role", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/staff - 409 Conflict on duplicate email")
    void testCreateStaff_DuplicateEmailConflict() throws Exception {
        CreateStaffRequest request1 = CreateStaffRequest.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .email("duplicate@caloryhive.com")
                .build();

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        CreateStaffRequest request2 = CreateStaffRequest.builder()
                .name("Alex Reed")
                .role("Sous Chef")
                .email("duplicate@caloryhive.com")
                .build();

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("DUPLICATE_RESOURCE")));
    }

    @Test
    @DisplayName("GET /api/staff - Retrieve all staff and filter by status and role")
    void testGetAllStaff_AndFiltering() throws Exception {
        CreateStaffRequest staff1 = CreateStaffRequest.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .status(StaffStatus.CLOCKED_IN)
                .email("jane@test.com")
                .build();

        CreateStaffRequest staff2 = CreateStaffRequest.builder()
                .name("Marcus Reed")
                .role("Sous Chef")
                .status(StaffStatus.OFF_DUTY)
                .email("marcus@test.com")
                .build();

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff1))).andExpect(status().isCreated());

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staff2))).andExpect(status().isCreated());

        // Get all
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Filter by status=CLOCKED_IN
        mockMvc.perform(get("/api/staff").param("status", "CLOCKED_IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Jane Smith")));

        // Filter by role=Sous Chef
        mockMvc.perform(get("/api/staff").param("role", "Sous Chef"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Marcus Reed")));
    }

    @Test
    @DisplayName("GET /api/staff/{id} - 200 OK on found, 404 on not found")
    void testGetStaffById() throws Exception {
        CreateStaffRequest request = CreateStaffRequest.builder()
                .name("Marcus Reed")
                .role("Sous Chef")
                .email("marcus.reed@test.com")
                .build();

        MvcResult result = mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String idStr = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        UUID id = UUID.fromString(idStr);

        mockMvc.perform(get("/api/staff/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Marcus Reed")));

        // Nonexistent ID -> 404
        mockMvc.perform(get("/api/staff/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("PUT /api/staff/{id} - Update staff details and 404 on missing")
    void testUpdateStaff() throws Exception {
        CreateStaffRequest createReq = CreateStaffRequest.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .status(StaffStatus.OFF_DUTY)
                .email("janesmith@test.com")
                .build();

        MvcResult result = mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String idStr = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        UpdateStaffRequest updateReq = UpdateStaffRequest.builder()
                .name("Jane S. Smith")
                .role("Executive Head Chef")
                .status(StaffStatus.CLOCKED_IN)
                .performance(BigDecimal.valueOf(4.99))
                .email("janesmith@test.com")
                .build();

        mockMvc.perform(put("/api/staff/" + idStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane S. Smith")))
                .andExpect(jsonPath("$.role", is("Executive Head Chef")))
                .andExpect(jsonPath("$.status", is("CLOCKED_IN")));

        // Update missing id -> 404
        mockMvc.perform(put("/api/staff/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/staff/{id} - 204 No Content on delete, 404 on missing")
    void testDeleteStaff() throws Exception {
        CreateStaffRequest createReq = CreateStaffRequest.builder()
                .name("Alex Reed")
                .role("Line Cook")
                .build();

        MvcResult result = mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String idStr = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete("/api/staff/" + idStr))
                .andExpect(status().isNoContent());

        // Second delete -> 404
        mockMvc.perform(delete("/api/staff/" + idStr))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/staff/search - Case-insensitive and empty keyword handling")
    void testSearchStaff() throws Exception {
        CreateStaffRequest s1 = CreateStaffRequest.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .stationArea("Kitchen Prep")
                .build();

        CreateStaffRequest s2 = CreateStaffRequest.builder()
                .name("Marcus Reed")
                .role("Sous Chef")
                .stationArea("Line Cooking")
                .build();

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s1))).andExpect(status().isCreated());

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s2))).andExpect(status().isCreated());

        // Case-insensitive search "jane"
        mockMvc.perform(get("/api/staff/search").param("keyword", "jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Jane Smith")));

        // Search by station "line"
        mockMvc.perform(get("/api/staff/search").param("keyword", "line"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Marcus Reed")));

        // Search empty keyword returns all
        mockMvc.perform(get("/api/staff/search").param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/staff/metrics - Returns accurate Total Headcount, Active Today, and Late/Absent")
    void testGetStaffMetrics() throws Exception {
        // Staff 1: Clocked in
        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateStaffRequest.builder()
                        .name("Jane Smith")
                        .role("Head Chef")
                        .status(StaffStatus.CLOCKED_IN)
                        .build()))).andExpect(status().isCreated());

        // Staff 2: Off duty
        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateStaffRequest.builder()
                        .name("Marcus Reed")
                        .role("Sous Chef")
                        .status(StaffStatus.OFF_DUTY)
                        .build()))).andExpect(status().isCreated());

        // Staff 3: Late
        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateStaffRequest.builder()
                        .name("Alex Reed")
                        .role("Line Cook")
                        .status(StaffStatus.LATE)
                        .build()))).andExpect(status().isCreated());

        mockMvc.perform(get("/api/staff/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHeadcount", is(3)))
                .andExpect(jsonPath("$.activeToday", is(1)))
                .andExpect(jsonPath("$.lateAbsent", is(1)));
    }

    @Test
    @DisplayName("GET /api should return 200 OK and service metadata")
    void shouldReturnApiRootMetadata() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", containsString("Staff Management")))
                .andExpect(jsonPath("$.endpoints.staff", is("/api/staff")))
                .andExpect(jsonPath("$.endpoints.shifts", is("/api/shifts")))
                .andExpect(jsonPath("$.endpoints.shiftRequests", is("/api/shift-requests")));
    }

    @Test
    @DisplayName("GET /unknown-path should return 404 Not Found rather than 500")
    void shouldHandleUnknownEndpointWith404Not500() throws Exception {
        mockMvc.perform(get("/api/unknown-nonexistent-path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")))
                .andExpect(jsonPath("$.status", is(404)));
    }
}
