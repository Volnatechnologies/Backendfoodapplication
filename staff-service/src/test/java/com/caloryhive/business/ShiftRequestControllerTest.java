package com.caloryhive.business;

import com.caloryhive.business.staff.dto.CreateShiftRequestDto;
import com.caloryhive.business.staff.dto.ReviewRequestDto;
import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.ShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.*;
import com.caloryhive.business.staff.repository.ShiftRepository;
import com.caloryhive.business.staff.repository.ShiftRequestRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ShiftRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShiftRequestRepository shiftRequestRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StaffRepository staffRepository;

    private Staff maria;
    private Staff alex;
    private Shift mariaFridayShift;

    @BeforeEach
    void setUp() {
        shiftRequestRepository.deleteAll();
        shiftRepository.deleteAll();
        staffRepository.deleteAll();

        maria = staffRepository.save(Staff.builder()
                .name("Maria Garcia")
                .role("Server")
                .status(StaffStatus.ACTIVE)
                .performance(BigDecimal.valueOf(4.80))
                .email("maria.garcia@caloryhive.com")
                .avatarInitials("MG")
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

        mariaFridayShift = shiftRepository.save(Shift.builder()
                .staff(maria)
                .shiftDate(LocalDate.of(2023, 10, 27))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(22, 0))
                .shiftRole("Server")
                .stationArea("Dining Area")
                .shiftType(ShiftType.REGULAR)
                .shiftCategory(ShiftCategory.EVENING)
                .status(ShiftStatus.PUBLISHED)
                .notes("Friday evening dinner shift")
                .build());
    }

    // 1. Valid SHIFT_SWAP creation -> 201 Created
    @Test
    @DisplayName("1. POST /api/shift-requests - Successfully create SHIFT_SWAP request with PENDING status")
    void test1_CreateShiftSwapRequest_Success() throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(maria.getId())
                .requestType(RequestType.SHIFT_SWAP)
                .shiftId(mariaFridayShift.getId())
                .targetStaffId(alex.getId())
                .reason("Wants to swap Friday Evening (Oct 27) with Alex R.")
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.staffName", is("Maria Garcia")))
                .andExpect(jsonPath("$.targetStaffName", is("Alex Reed")))
                .andExpect(jsonPath("$.requestType", is("SHIFT_SWAP")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.shiftSummary", containsString("2023-10-27")));
    }

    // 2. Valid TIME_OFF creation -> 201 Created
    @Test
    @DisplayName("2. POST /api/shift-requests - Successfully create TIME_OFF request with PENDING status")
    void test2_CreateTimeOffRequest_Success() throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(maria.getId())
                .requestType(RequestType.TIME_OFF)
                .requestedDate(LocalDate.of(2023, 11, 2))
                .endDate(LocalDate.of(2023, 11, 4))
                .reason("Requesting Nov 2nd - Nov 4th off for family event.")
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.staffName", is("Maria Garcia")))
                .andExpect(jsonPath("$.requestType", is("TIME_OFF")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.reason", containsString("family event")));
    }

    // 3. Shift swap with invalid staff -> 404
    @Test
    @DisplayName("3. POST /api/shift-requests - Invalid requesting staff returns 404")
    void test3_CreateShiftSwap_InvalidStaff() throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(UUID.randomUUID())
                .requestType(RequestType.SHIFT_SWAP)
                .shiftId(mariaFridayShift.getId())
                .targetStaffId(alex.getId())
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    // 4. Shift swap with invalid shift -> 404
    @Test
    @DisplayName("4. POST /api/shift-requests - Invalid shift returns 404")
    void test4_CreateShiftSwap_InvalidShift() throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(maria.getId())
                .requestType(RequestType.SHIFT_SWAP)
                .shiftId(UUID.randomUUID())
                .targetStaffId(alex.getId())
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    // 5. Missing required fields -> 400
    @Test
    @DisplayName("5. POST /api/shift-requests - Missing required fields returns 400")
    void test5_CreateShiftRequest_MissingRequiredFields() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")));
    }

    // 6. Invalid request type -> 400
    @Test
    @DisplayName("6. POST /api/shift-requests - Invalid request type returns 400")
    void test6_CreateShiftRequest_InvalidRequestType() throws Exception {
        String invalidJson = "{\"staffId\":\"" + maria.getId() + "\",\"requestType\":\"INVALID_TYPE\",\"reason\":\"test\"}";

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // 7. Requesting swap with self -> 400
    @Test
    @DisplayName("7. POST /api/shift-requests - Requesting swap with self returns 400")
    void test7_CreateShiftSwap_SameStaff() throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(maria.getId())
                .requestType(RequestType.SHIFT_SWAP)
                .shiftId(mariaFridayShift.getId())
                .targetStaffId(maria.getId())
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cannot swap shift with yourself")));
    }

    // 8. GET all requests -> 200
    @Test
    @DisplayName("8. GET /api/shift-requests - Retrieve all requests returns 200")
    void test8_GetAllRequests() throws Exception {
        createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap 1");
        createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Doctor");

        mockMvc.perform(get("/api/shift-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // 9. GET request by ID -> 200
    @Test
    @DisplayName("9. GET /api/shift-requests/{id} - Retrieve request by ID returns 200")
    void test9_GetRequestById() throws Exception {
        MvcResult res = createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap details");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/shift-requests/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.staffName", is("Maria Garcia")));
    }

    // 10. GET request by invalid ID -> 404
    @Test
    @DisplayName("10. GET /api/shift-requests/{id} - 404 on missing request ID")
    void test10_GetRequestById_NotFound() throws Exception {
        mockMvc.perform(get("/api/shift-requests/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));
    }

    // 11. Filter by PENDING status
    @Test
    @DisplayName("11. GET /api/shift-requests?status=PENDING - Filter by PENDING")
    void test11_FilterByPendingStatus() throws Exception {
        createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Pending swap");

        mockMvc.perform(get("/api/shift-requests").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    // 12. Filter by staff
    @Test
    @DisplayName("12. GET /api/shift-requests?staffId={id} - Filter by staff ID")
    void test12_FilterByStaff() throws Exception {
        createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Maria's request");
        createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Alex's request");

        mockMvc.perform(get("/api/shift-requests").param("staffId", alex.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffName", is("Alex Reed")));
    }

    // 13. Filter by SHIFT_SWAP
    @Test
    @DisplayName("13. GET /api/shift-requests?type=SHIFT_SWAP - Filter by SHIFT_SWAP")
    void test13_FilterByShiftSwap() throws Exception {
        createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap request");
        createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");

        mockMvc.perform(get("/api/shift-requests").param("type", "SHIFT_SWAP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requestType", is("SHIFT_SWAP")));
    }

    // 14. Filter by TIME_OFF
    @Test
    @DisplayName("14. GET /api/shift-requests?type=TIME_OFF - Filter by TIME_OFF")
    void test14_FilterByTimeOff() throws Exception {
        createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap request");
        createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");

        mockMvc.perform(get("/api/shift-requests").param("type", "TIME_OFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requestType", is("TIME_OFF")));
    }

    // 15. Approve pending request -> success
    @Test
    @DisplayName("15. PUT /api/shift-requests/{id}/approve - Approve pending request succeeds")
    void test15_ApprovePendingRequest_Success() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        ReviewRequestDto review = ReviewRequestDto.builder().adminComment("Approved by Admin").build();

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.adminComment", is("Approved by Admin")));
    }

    // 16. Deny pending request -> success
    @Test
    @DisplayName("16. PUT /api/shift-requests/{id}/deny - Deny pending request succeeds")
    void test16_DenyPendingRequest_Success() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        ReviewRequestDto review = ReviewRequestDto.builder().adminComment("Denied due to coverage").build();

        mockMvc.perform(put("/api/shift-requests/" + id + "/deny")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DENIED")))
                .andExpect(jsonPath("$.adminComment", is("Denied due to coverage")));
    }

    // 17. Approve already approved request -> 409
    @Test
    @DisplayName("17. PUT /api/shift-requests/{id}/approve - Approve already approved request returns 409")
    void test17_ApproveAlreadyApproved_Returns409() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve")).andExpect(status().isOk());

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Cannot approve an already approved request")));
    }

    // 18. Deny already denied request -> 409
    @Test
    @DisplayName("18. PUT /api/shift-requests/{id}/deny - Deny already denied request returns 409")
    void test18_DenyAlreadyDenied_Returns409() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/shift-requests/" + id + "/deny")).andExpect(status().isOk());

        mockMvc.perform(put("/api/shift-requests/" + id + "/deny"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Cannot deny an already denied request")));
    }

    // 19. Approve denied request -> 409
    @Test
    @DisplayName("19. PUT /api/shift-requests/{id}/approve - Approve denied request returns 409")
    void test19_ApproveDenied_Returns409() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/shift-requests/" + id + "/deny")).andExpect(status().isOk());

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Cannot approve an already denied request")));
    }

    // 20. Deny approved request -> 409
    @Test
    @DisplayName("20. PUT /api/shift-requests/{id}/deny - Deny approved request returns 409")
    void test20_DenyApproved_Returns409() throws Exception {
        MvcResult res = createTestRequest(alex.getId(), RequestType.TIME_OFF, null, null, LocalDate.of(2023, 11, 10), "Time off");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve")).andExpect(status().isOk());

        mockMvc.perform(put("/api/shift-requests/" + id + "/deny"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Cannot deny an already approved request")));
    }

    // 21. Invalid/conflicting shift swap (target staff has overlapping shift) -> 409
    @Test
    @DisplayName("21. POST /api/shift-requests - Conflicting shift swap returns 409")
    void test21_ConflictingShiftSwap_Returns409() throws Exception {
        // Give Alex an overlapping shift
        shiftRepository.save(Shift.builder()
                .staff(alex)
                .shiftDate(LocalDate.of(2023, 10, 27))
                .startTime(LocalTime.of(17, 0))
                .endTime(LocalTime.of(23, 0))
                .shiftRole("Sous Chef")
                .stationArea("Line")
                .shiftType(ShiftType.REGULAR)
                .status(ShiftStatus.PUBLISHED)
                .build());

        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(maria.getId())
                .requestType(RequestType.SHIFT_SWAP)
                .shiftId(mariaFridayShift.getId())
                .targetStaffId(alex.getId())
                .build();

        mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("overlapping shift")));
    }

    // 22. Successful shift swap updates staff assignment
    @Test
    @DisplayName("22. PUT /api/shift-requests/{id}/approve - Successful shift swap updates shift staff assignment")
    void test22_SuccessfulShiftSwap_UpdatesStaffAssignment() throws Exception {
        MvcResult res = createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap Friday with Alex");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/shift-requests/" + id + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        Shift reassignedShift = shiftRepository.findById(mariaFridayShift.getId()).orElseThrow();
        assertEquals(alex.getId(), reassignedShift.getStaff().getId(), "Shift must now be assigned to Alex Reed");
    }

    // 23. PostgreSQL persistence verification
    @Test
    @org.springframework.transaction.annotation.Transactional
    @DisplayName("23. ShiftRequest repository persists and retrieves entity correctly")
    void test23_PersistenceVerification() {
        ShiftRequest entity = ShiftRequest.builder()
                .staff(maria)
                .requestType(RequestType.TIME_OFF)
                .requestedDate(LocalDate.of(2023, 12, 1))
                .endDate(LocalDate.of(2023, 12, 5))
                .reason("Year-end holiday")
                .status(RequestStatus.PENDING)
                .build();

        ShiftRequest saved = shiftRequestRepository.save(entity);
        assertNotNull(saved.getId());

        ShiftRequest retrieved = shiftRequestRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Maria Garcia", retrieved.getStaff().getName());
        assertEquals(RequestType.TIME_OFF, retrieved.getRequestType());
        assertEquals(RequestStatus.PENDING, retrieved.getStatus());
        assertEquals("Year-end holiday", retrieved.getReason());
    }

    // 24. Transaction rollback on failed swap
    @Test
    @DisplayName("24. PUT /api/shift-requests/{id}/approve - Transaction integrity maintained if approval fails")
    void test24_TransactionIntegrity_OnApprovalFailure() throws Exception {
        // Create valid shift swap request while Alex has no conflicts
        MvcResult res = createTestRequest(maria.getId(), RequestType.SHIFT_SWAP, mariaFridayShift.getId(), alex.getId(), null, "Swap Friday");
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();

        // Now introduce a conflict for Alex AFTER the request was created
        shiftRepository.save(Shift.builder()
                .staff(alex)
                .shiftDate(LocalDate.of(2023, 10, 27))
                .startTime(LocalTime.of(19, 0))
                .endTime(LocalTime.of(21, 0))
                .shiftRole("Sous Chef")
                .stationArea("Kitchen")
                .shiftType(ShiftType.REGULAR)
                .status(ShiftStatus.PUBLISHED)
                .build());

        // Attempting to approve must fail with 409 Conflict due to the new conflict
        mockMvc.perform(put("/api/shift-requests/" + id + "/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("CONFLICT")));

        // Verify request remains PENDING and shift owner is still Maria (rolled back)
        ShiftRequest request = shiftRequestRepository.findById(UUID.fromString(id)).orElseThrow();
        assertEquals(RequestStatus.PENDING, request.getStatus());

        Shift shift = shiftRepository.findById(mariaFridayShift.getId()).orElseThrow();
        assertEquals(maria.getId(), shift.getStaff().getId());
    }

    private MvcResult createTestRequest(UUID staffId, RequestType type, UUID shiftId, UUID targetStaffId, LocalDate requestedDate, String reason) throws Exception {
        CreateShiftRequestDto dto = CreateShiftRequestDto.builder()
                .staffId(staffId)
                .requestType(type)
                .shiftId(shiftId)
                .targetStaffId(targetStaffId)
                .requestedDate(requestedDate)
                .reason(reason)
                .build();

        return mockMvc.perform(post("/api/shift-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
