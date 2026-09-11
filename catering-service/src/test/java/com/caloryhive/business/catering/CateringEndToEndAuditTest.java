package com.caloryhive.business.catering;

import com.caloryhive.business.auth.dto.AuthResponse;
import com.caloryhive.business.auth.dto.RegisterRequest;
import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.entity.CateringBooking;
import com.caloryhive.business.catering.entity.CateringMenuPackage;
import com.caloryhive.business.catering.repository.CateringBookingRepository;
import com.caloryhive.business.catering.repository.CateringMenuPackageRepository;
import com.caloryhive.business.catering.service.PricingService;
import com.caloryhive.business.common.enums.BookingStatus;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CateringEndToEndAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CateringBookingRepository bookingRepository;

    @Autowired
    private CateringMenuPackageRepository packageRepository;

    @Autowired
    private PricingService pricingService;

    private String userToken;
    private String otherUserToken;

    private final UUID eventTypeId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID packageId = UUID.fromString("11111111-2222-3333-4444-555555555555"); // Corporate Bites ($45)
    private final UUID beverageOptionId = UUID.fromString("cccccccc-1111-2222-3333-444444444444"); // $8
    private final UUID dessertOptionId = UUID.fromString("cccccccc-2222-3333-4444-555555555555"); // $5

    @BeforeEach
    void setup() throws Exception {
        // Register primary user
        String email = "audit.user." + UUID.randomUUID() + "@example.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Audit");
        registerRequest.setLastName("User");
        registerRequest.setEmail(email);
        registerRequest.setPassword("Password123!");

        MvcResult res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(res.getResponse().getContentAsString(), AuthResponse.class);
        userToken = "Bearer " + auth.getToken();

        // Register secondary user for unauthorized access tests
        String otherEmail = "other.user." + UUID.randomUUID() + "@example.com";
        RegisterRequest otherRequest = new RegisterRequest();
        otherRequest.setFirstName("Other");
        otherRequest.setLastName("User");
        otherRequest.setEmail(otherEmail);
        otherRequest.setPassword("Password123!");

        MvcResult otherRes = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse otherAuth = objectMapper.readValue(otherRes.getResponse().getContentAsString(), AuthResponse.class);
        otherUserToken = "Bearer " + otherAuth.getToken();
    }

    private CreateBookingRequest buildValidBookingRequest() {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setEventName("TechCorp Annual Gala");
        req.setEventTypeId(eventTypeId);
        req.setEventDate(LocalDate.now().plusDays(30));
        req.setEventTime(LocalTime.of(18, 0));
        req.setGuestCount(100);
        req.setVenueAddress("742 Evergreen Terrace, Springfield, IL 62704");
        req.setSpecialInstructions("Gluten-free meals requested for 5 guests");
        req.setMenuPackageId(packageId);
        req.setCustomOptionIds(List.of(beverageOptionId, dessertOptionId));
        return req;
    }

    // =========================================================================
    // 1. EMPTY EVENT NAME
    // =========================================================================
    @Test
    @DisplayName("Test 1: Empty event name returns 400 VALIDATION_ERROR")
    void test01_emptyEventName() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setEventName("");

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.eventName").exists());
    }

    // =========================================================================
    // 2. INVALID EVENT TYPE
    // =========================================================================
    @Test
    @DisplayName("Test 2: Invalid event type returns 404 RESOURCE_NOT_FOUND")
    void test02_invalidEventType() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setEventTypeId(UUID.randomUUID());

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(containsString("NOT_FOUND")));
    }

    // =========================================================================
    // 3. PAST EVENT DATE
    // =========================================================================
    @Test
    @DisplayName("Test 3: Past event date returns 400 INVALID_DATE_TIME / VALIDATION_ERROR")
    void test03_pastEventDate() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setEventDate(LocalDate.now().minusDays(5));

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // 4. INVALID TIME
    // =========================================================================
    @Test
    @DisplayName("Test 4: Invalid time format returns 400 INVALID_REQUEST_BODY")
    void test04_invalidTime() throws Exception {
        String invalidJson = """
            {
                "eventName": "Annual Dinner",
                "eventTypeId": "%s",
                "eventDate": "%s",
                "eventTime": "25:99:99",
                "guestCount": 50,
                "venueAddress": "123 Main St",
                "menuPackageId": "%s"
            }
            """.formatted(eventTypeId, LocalDate.now().plusDays(10), packageId);

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_BODY"));
    }

    // =========================================================================
    // 5. GUEST COUNT = 0
    // =========================================================================
    @Test
    @DisplayName("Test 5: Guest count = 0 returns 400 INVALID_GUEST_COUNT / VALIDATION_ERROR")
    void test05_guestCountZero() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setGuestCount(0);

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // 6. NEGATIVE GUEST COUNT
    // =========================================================================
    @Test
    @DisplayName("Test 6: Negative guest count returns 400 INVALID_GUEST_COUNT / VALIDATION_ERROR")
    void test06_negativeGuestCount() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setGuestCount(-25);

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =========================================================================
    // 7. MISSING VENUE
    // =========================================================================
    @Test
    @DisplayName("Test 7: Missing venue address returns 400 VALIDATION_ERROR")
    void test07_missingVenue() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setVenueAddress("");

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.venueAddress").exists());
    }

    // =========================================================================
    // 8. MISSING MENU PACKAGE
    // =========================================================================
    @Test
    @DisplayName("Test 8: Missing menu package returns 400 VALIDATION_ERROR")
    void test08_missingMenuPackage() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setMenuPackageId(null);

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.menuPackageId").exists());
    }

    // =========================================================================
    // 9. INVALID PACKAGE ID
    // =========================================================================
    @Test
    @DisplayName("Test 9: Invalid package ID returns 404 PACKAGE_NOT_FOUND")
    void test09_invalidPackageId() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setMenuPackageId(UUID.randomUUID());

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(containsString("NOT_FOUND")));
    }

    // =========================================================================
    // 10. INVALID CUSTOM OPTION
    // =========================================================================
    @Test
    @DisplayName("Test 10: Invalid custom option ID returns 404 CUSTOM_OPTION_NOT_FOUND")
    void test10_invalidCustomOption() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setCustomOptionIds(List.of(UUID.randomUUID()));

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(containsString("NOT_FOUND")));
    }

    // =========================================================================
    // 11. NEGATIVE PRICE
    // =========================================================================
    @Test
    @DisplayName("Test 11: Negative pricing rejected with IllegalArgumentException")
    void test11_negativePrice() {
        CateringMenuPackage pkg = new CateringMenuPackage();
        pkg.setPricePerGuest(new BigDecimal("-15.00"));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(pkg, List.of(), 50);
        });
    }

    // =========================================================================
    // 12. DUPLICATE SUBMISSION / ID GENERATION
    // =========================================================================
    @Test
    @DisplayName("Test 12: Multiple submissions create distinct booking codes")
    void test12_duplicateSubmission() throws Exception {
        CreateBookingRequest req1 = buildValidBookingRequest();
        CreateBookingRequest req2 = buildValidBookingRequest();

        MvcResult res1 = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult res2 = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse b1 = objectMapper.readValue(res1.getResponse().getContentAsString(), BookingDetailsResponse.class);
        BookingDetailsResponse b2 = objectMapper.readValue(res2.getResponse().getContentAsString(), BookingDetailsResponse.class);

        assertThat(b1.getId()).isNotEqualTo(b2.getId());
        assertThat(b1.getBookingCode()).isNotEqualTo(b2.getBookingCode());
    }

    // =========================================================================
    // 13. CONFIRM NONEXISTENT BOOKING
    // =========================================================================
    @Test
    @DisplayName("Test 13: Confirm nonexistent booking returns 404 BOOKING_NOT_FOUND")
    void test13_confirmNonexistentBooking() throws Exception {
        ConfirmBookingRequest confirmReq = new ConfirmBookingRequest();
        confirmReq.setNotes("Test confirm");

        mockMvc.perform(post("/api/catering/bookings/" + UUID.randomUUID() + "/confirm")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("BOOKING_NOT_FOUND"));
    }

    // =========================================================================
    // 14. INVALID STATUS TRANSITION
    // =========================================================================
    @Test
    @DisplayName("Test 14: Confirming an already confirmed booking returns 400 INVALID_BOOKING_STATUS")
    void test14_invalidStatusTransition() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();

        MvcResult createRes = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(createRes.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // First confirmation -> succeeds
        mockMvc.perform(post("/api/catering/bookings/" + created.getId() + "/confirm")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Second confirmation -> rejected
        mockMvc.perform(post("/api/catering/bookings/" + created.getId() + "/confirm")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_BOOKING_STATUS"))
                .andExpect(jsonPath("$.message").value(containsString("already confirmed")));
    }

    // =========================================================================
    // 15. UNAUTHORIZED BOOKING ACCESS
    // =========================================================================
    @Test
    @DisplayName("Test 15: Unauthorized user cannot update or confirm another user's booking")
    void test15_unauthorizedBookingAccess() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();

        MvcResult createRes = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(createRes.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // Attempt confirm by other user -> 403 Forbidden
        mockMvc.perform(post("/api/catering/bookings/" + created.getId() + "/confirm")
                        .header("Authorization", otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        // Attempt get booking details by other user -> 403 Forbidden
        mockMvc.perform(get("/api/catering/bookings/" + created.getId())
                        .header("Authorization", otherUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        // Attempt get financial summary by other user -> 403 Forbidden
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/financial-summary")
                        .header("Authorization", otherUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        // Attempt get confirmation by other user -> 403 Forbidden
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/confirmation")
                        .header("Authorization", otherUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    // =========================================================================
    // 16. DATABASE ERROR HANDLING (NO CREDENTIALS / STACK LEAK)
    // =========================================================================
    @Test
    @DisplayName("Test 16: Error responses never expose stack traces or DB credentials")
    void test16_databaseErrorHandling() throws Exception {
        MvcResult res = mockMvc.perform(get("/api/catering/bookings/invalid-uuid")
                        .header("Authorization", userToken))
                .andExpect(status().isBadRequest())
                .andReturn();

        String body = res.getResponse().getContentAsString();
        assertThat(body).doesNotContain("password");
        assertThat(body).doesNotContain("jdbc");
        assertThat(body).doesNotContain("at org.springframework");
    }

    // =========================================================================
    // 17. INVALID JSON REQUEST
    // =========================================================================
    @Test
    @DisplayName("Test 17: Malformed JSON syntax returns 400 INVALID_REQUEST_BODY")
    void test17_invalidJsonRequest() throws Exception {
        String brokenJson = "{ \"eventName\": \"broken json, ";

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_BODY"));
    }

    // =========================================================================
    // 18. VALIDATION ERRORS SCHEMA
    // =========================================================================
    @Test
    @DisplayName("Test 18: Multiple validation errors populate fieldErrors map")
    void test18_validationErrors() throws Exception {
        CreateBookingRequest req = new CreateBookingRequest(); // completely empty

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.eventName").exists())
                .andExpect(jsonPath("$.fieldErrors.eventDate").exists())
                .andExpect(jsonPath("$.fieldErrors.guestCount").exists());
    }

    // =========================================================================
    // 19. LARGE GUEST COUNT & PRICING PRECISION
    // =========================================================================
    @Test
    @DisplayName("Test 19: Large guest count (5,000) calculates exact BigDecimal totals")
    void test19_largeGuestCount() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setGuestCount(5000);
        req.setCustomOptionIds(List.of()); // only Corporate Bites at $45

        MvcResult res = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse b = objectMapper.readValue(res.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // 5,000 * $45 = $225,000.00 basePrice
        // Service fee 10% = $22,500.00
        // Tax 8.25% of ($225,000 + $22,500) = $20,418.75
        // Final Total = $267,918.75
        // Deposit 30% = $80,375.63
        assertThat(b.getPricing().getBasePrice()).isEqualByComparingTo(new BigDecimal("225000.00"));
        assertThat(b.getPricing().getServiceFee()).isEqualByComparingTo(new BigDecimal("22500.00"));
        assertThat(b.getPricing().getTax()).isEqualByComparingTo(new BigDecimal("20418.75"));
        assertThat(b.getPricing().getFinalTotal()).isEqualByComparingTo(new BigDecimal("267918.75"));
        assertThat(b.getPricing().getDepositRequired()).isEqualByComparingTo(new BigDecimal("80375.63"));
    }

    // =========================================================================
    // 20. MULTIPLE CUSTOM OPTIONS
    // =========================================================================
    @Test
    @DisplayName("Test 20: Multiple custom options sum properly into subtotal and fees")
    void test20_multipleCustomOptions() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setGuestCount(100);
        req.setCustomOptionIds(List.of(beverageOptionId, dessertOptionId)); // $8 + $5 = $13

        MvcResult res = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse b = objectMapper.readValue(res.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // basePrice: 100 * $45 = $4,500.00
        // customOptions: $8 + $5 = $13.00
        // subtotal: $4,513.00
        assertThat(b.getPricing().getCustomOptionsTotal()).isEqualByComparingTo(new BigDecimal("13.00"));
        assertThat(b.getPricing().getSubtotal()).isEqualByComparingTo(new BigDecimal("4513.00"));
    }

    // =========================================================================
    // 21. DRAFT BOOKING
    // =========================================================================
    @Test
    @DisplayName("Test 21: Draft booking saved with partial data has DRAFT status")
    void test21_draftBooking() throws Exception {
        DraftBookingRequest draftReq = new DraftBookingRequest();
        draftReq.setEventName("Tentative Executive Lunch");
        draftReq.setGuestCount(20);

        MvcResult res = mockMvc.perform(post("/api/catering/bookings/draft")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("Tentative Executive Lunch"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        BookingDetailsResponse savedDraft = objectMapper.readValue(res.getResponse().getContentAsString(), BookingDetailsResponse.class);
        assertThat(savedDraft.getStatus()).isEqualTo(BookingStatus.DRAFT);
    }

    // =========================================================================
    // 22. CONFIRMED BOOKING FLOW
    // =========================================================================
    @Test
    @DisplayName("Test 22: Complete booking confirmation flow records status and deposit")
    void test22_confirmedBooking() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();

        MvcResult createRes = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(createRes.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // Confirm
        ConfirmBookingRequest confirmReq = new ConfirmBookingRequest();
        confirmReq.setPaymentReference("PAY-ONLINE-999");
        confirmReq.setNotes("Confirmed by client via online portal");

        MvcResult confirmRes = mockMvc.perform(post("/api/catering/bookings/" + created.getId() + "/confirm")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.bookingCode").isString())
                .andExpect(jsonPath("$.totalValue").isNumber())
                .andExpect(jsonPath("$.nextSteps").isArray())
                .andReturn();

        BookingConfirmationResponse conf = objectMapper.readValue(confirmRes.getResponse().getContentAsString(), BookingConfirmationResponse.class);
        assertThat(conf.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(conf.getNextSteps()).isNotEmpty();
    }

    // =========================================================================
    // 23. DASHBOARD WITH NO BOOKINGS
    // =========================================================================
    @Test
    @DisplayName("Test 23: Dashboard endpoint handles zero/empty bookings gracefully")
    void test23_dashboardWithNoBookings() throws Exception {
        mockMvc.perform(get("/api/catering/dashboard")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upcomingEvents").isNumber())
                .andExpect(jsonPath("$.totalRevenue").isNumber())
                .andExpect(jsonPath("$.activeBookings").isArray());
    }

    // =========================================================================
    // 24. DASHBOARD WITH MULTIPLE BOOKINGS
    // =========================================================================
    @Test
    @DisplayName("Test 24: Dashboard aggregates metrics accurately across multiple bookings")
    void test24_dashboardWithMultipleBookings() throws Exception {
        // Create two bookings
        CreateBookingRequest req1 = buildValidBookingRequest();
        req1.setEventName("Company Gala A");
        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        CreateBookingRequest req2 = buildValidBookingRequest();
        req2.setEventName("Company Gala B");
        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated());

        // Get dashboard
        mockMvc.perform(get("/api/catering/dashboard")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeBookings", hasSize(greaterThanOrEqualTo(2))));
    }

    // =========================================================================
    // 25. EXACT PRICING CALCULATION - CORPORATE BITES (150 GUESTS)
    // =========================================================================
    @Test
    @DisplayName("Test 25: Corporate Bites with 150 guests matches exact expected financial breakdown")
    void test25_exactFinancialCalculation_CorporateBites_150Guests() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setMenuPackageId(UUID.fromString("11111111-2222-3333-4444-555555555555")); // Corporate Bites ($45)
        req.setGuestCount(150);
        req.setCustomOptionIds(List.of()); // No custom options

        MvcResult res = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pricing.subtotal").value(6750.00))
                .andExpect(jsonPath("$.pricing.serviceFee").value(675.00))
                .andExpect(jsonPath("$.pricing.tax").value(612.56))
                .andExpect(jsonPath("$.pricing.finalTotal").value(8037.56))
                .andExpect(jsonPath("$.pricing.depositRequired").value(2411.27))
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // Verify financial summary endpoint returns exact same values
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/financial-summary")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(6750.00))
                .andExpect(jsonPath("$.serviceFee").value(675.00))
                .andExpect(jsonPath("$.tax").value(612.56))
                .andExpect(jsonPath("$.finalTotal").value(8037.56))
                .andExpect(jsonPath("$.depositRequired").value(2411.27));
    }

    // =========================================================================
    // 26. EXACT PRICING CALCULATION - PREMIUM PLATED (150 GUESTS)
    // =========================================================================
    @Test
    @DisplayName("Test 26: Premium Plated ($120) with 150 guests matches exact expected financial breakdown")
    void test26_exactFinancialCalculation_PremiumPlated_150Guests() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();
        req.setMenuPackageId(UUID.fromString("11111111-2222-3333-4444-666666666666")); // Premium Plated ($120)
        req.setGuestCount(150);
        req.setCustomOptionIds(List.of()); // No custom options

        MvcResult res = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pricing.subtotal").value(18000.00))
                .andExpect(jsonPath("$.pricing.serviceFee").value(1800.00))
                .andExpect(jsonPath("$.pricing.tax").value(1633.50))
                .andExpect(jsonPath("$.pricing.finalTotal").value(21433.50))
                .andExpect(jsonPath("$.pricing.depositRequired").value(6430.05))
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // Verify financial summary endpoint returns exact same values
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/financial-summary")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(18000.00))
                .andExpect(jsonPath("$.serviceFee").value(1800.00))
                .andExpect(jsonPath("$.tax").value(1633.50))
                .andExpect(jsonPath("$.finalTotal").value(21433.50))
                .andExpect(jsonPath("$.depositRequired").value(6430.05));
    }

    // =========================================================================
    // 27. TIMELINE AND UPDATE AUTHORIZATION ENFORCED
    // =========================================================================
    @Test
    @DisplayName("Test 27: Timeline and Update endpoints strictly enforce authorization")
    void test27_timelineAndUpdateAuthorizationEnforced() throws Exception {
        CreateBookingRequest req = buildValidBookingRequest();

        MvcResult createRes = mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDetailsResponse created = objectMapper.readValue(createRes.getResponse().getContentAsString(), BookingDetailsResponse.class);

        // Attempt get timeline by unauthorized user -> 403 Forbidden
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/timeline")
                        .header("Authorization", otherUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        // Attempt update by unauthorized user -> 403 Forbidden
        UpdateBookingRequest updateReq = new UpdateBookingRequest();
        updateReq.setEventName("Hacked Event Name");
        updateReq.setEventTypeId(eventTypeId);
        updateReq.setEventDate(LocalDate.now().plusDays(30));
        updateReq.setEventTime(LocalTime.of(18, 0));
        updateReq.setGuestCount(100);
        updateReq.setVenueAddress("742 Evergreen Terrace, Springfield, IL 62704");
        updateReq.setMenuPackageId(packageId);
        mockMvc.perform(put("/api/catering/bookings/" + created.getId())
                        .header("Authorization", otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        // Authorized user accesses timeline -> 200 OK
        mockMvc.perform(get("/api/catering/bookings/" + created.getId() + "/timeline")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}

