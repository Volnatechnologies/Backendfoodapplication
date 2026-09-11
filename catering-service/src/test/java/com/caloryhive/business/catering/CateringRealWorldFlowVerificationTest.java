package com.caloryhive.business.catering;

import com.caloryhive.business.auth.dto.AuthResponse;
import com.caloryhive.business.auth.dto.RegisterRequest;
import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.entity.CateringBooking;
import com.caloryhive.business.catering.repository.CateringBookingRepository;
import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.payment.entity.Payment;
import com.caloryhive.business.payment.repository.PaymentRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Step 10: Final Real-World Verification Test Suite
 * Validates the complete 22-step flow end-to-end on live runtime.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CateringRealWorldFlowVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CateringBookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private String userToken;
    private final UUID eventTypeId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID packageId = UUID.fromString("11111111-2222-3333-4444-555555555555"); // Corporate Bites ($45.00)
    private final UUID beverageOptionId = UUID.fromString("cccccccc-1111-2222-3333-444444444444"); // $8.00
    private final UUID dessertOptionId = UUID.fromString("cccccccc-2222-3333-4444-555555555555"); // $5.00

    @BeforeEach
    void authenticate() throws Exception {
        String email = "realworld." + UUID.randomUUID() + "@example.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Sarah");
        registerRequest.setLastName("Connor");
        registerRequest.setEmail(email);
        registerRequest.setPassword("Password123!");

        MvcResult res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse auth = objectMapper.readValue(res.getResponse().getContentAsString(), AuthResponse.class);
        userToken = "Bearer " + auth.getToken();
    }

    @Test
    @DisplayName("Complete Real-World End-to-End Catering Lifecycle Flow")
    void testCompleteCateringFlow() throws Exception {

        // =====================================================================
        // STEP 1 & 2: Open Catering Dashboard & Verify Real DB Metrics
        // =====================================================================
        MvcResult dashRes1 = mockMvc.perform(get("/api/catering/dashboard")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upcomingEvents").isNumber())
                .andExpect(jsonPath("$.totalRevenue").isNumber())
                .andExpect(jsonPath("$.activeBookings").isArray())
                .andReturn();

        DashboardResponse initialDashboard = objectMapper.readValue(dashRes1.getResponse().getContentAsString(), DashboardResponse.class);
        long initialUpcoming = initialDashboard.getUpcomingEvents();
        BigDecimal initialRevenue = initialDashboard.getTotalRevenue();

        // =====================================================================
        // STEP 3, 4, 5: Fetch Master Data (Event Types, Packages, Options)
        // =====================================================================
        mockMvc.perform(get("/api/catering/event-types")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/api/catering/packages")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Corporate Bites"))
                .andExpect(jsonPath("$[0].pricePerGuest").value(45.00));

        mockMvc.perform(get("/api/catering/custom-options")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").isNumber());

        // =====================================================================
        // STEP 6: Real-Time Pricing Calculation API (120 guests, Corp Bites + Addons)
        // =====================================================================
        PriceCalculationRequest calcReq = new PriceCalculationRequest();
        calcReq.setMenuPackageId(packageId);
        calcReq.setGuestCount(120);
        calcReq.setCustomOptionIds(List.of(beverageOptionId, dessertOptionId)); // $8 + $5 = $13

        MvcResult calcRes = mockMvc.perform(post("/api/catering/bookings/calculate-price")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(calcReq)))
                .andExpect(status().isOk())
                .andReturn();

        FinancialSummaryResponse calcSummary = objectMapper.readValue(calcRes.getResponse().getContentAsString(), FinancialSummaryResponse.class);
        // Base: 120 * 45 = $5,400.00
        // Custom options: $13.00
        // Subtotal: $5,413.00
        // Service Fee 10%: $541.30
        // Tax 8.25%: $491.23
        // Final Total: $6,445.53
        // Deposit 30%: $1,933.66
        assertThat(calcSummary.getBasePrice()).isEqualByComparingTo(new BigDecimal("5400.00"));
        assertThat(calcSummary.getCustomOptionsTotal()).isEqualByComparingTo(new BigDecimal("13.00"));
        assertThat(calcSummary.getSubtotal()).isEqualByComparingTo(new BigDecimal("5413.00"));
        assertThat(calcSummary.getServiceFee()).isEqualByComparingTo(new BigDecimal("541.30"));
        assertThat(calcSummary.getTax()).isEqualByComparingTo(new BigDecimal("491.23"));
        assertThat(calcSummary.getFinalTotal()).isEqualByComparingTo(new BigDecimal("6445.53"));
        assertThat(calcSummary.getDepositRequired()).isEqualByComparingTo(new BigDecimal("1933.66"));

        // =====================================================================
        // STEP 7 & 8: Save as Draft & Verify Database Persistence
        // =====================================================================
        DraftBookingRequest draftReq = new DraftBookingRequest();
        draftReq.setEventName("Springfield Tech Expo 2026");
        draftReq.setEventTypeId(eventTypeId);
        draftReq.setEventDate(LocalDate.now().plusDays(1));
        draftReq.setEventTime(LocalTime.of(17, 30));
        draftReq.setGuestCount(120);
        draftReq.setVenueAddress("742 Evergreen Terrace, Springfield, IL 62704");
        draftReq.setSpecialInstructions("Vegan options for 10 attendees");
        draftReq.setMenuPackageId(packageId);
        draftReq.setCustomOptionIds(List.of(beverageOptionId, dessertOptionId));

        MvcResult draftRes = mockMvc.perform(post("/api/catering/bookings/draft")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventName").value("Springfield Tech Expo 2026"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        BookingDetailsResponse savedDraft = objectMapper.readValue(draftRes.getResponse().getContentAsString(), BookingDetailsResponse.class);
        UUID bookingId = savedDraft.getId();
        assertThat(bookingId).isNotNull();

        // Verify entity exists directly in database repository
        Optional<CateringBooking> dbDraftOpt = bookingRepository.findById(bookingId);
        assertThat(dbDraftOpt).isPresent();
        CateringBooking dbDraft = dbDraftOpt.get();
        assertThat(dbDraft.getStatus()).isEqualTo(BookingStatus.DRAFT);
        assertThat(dbDraft.getEventName()).isEqualTo("Springfield Tech Expo 2026");
        assertThat(dbDraft.getGuestCount()).isEqualTo(120);
        assertThat(dbDraft.getFinalTotal()).isEqualByComparingTo(new BigDecimal("6445.53"));

        // =====================================================================
        // STEP 9: Re-open the booking by ID & Verify All Values
        // =====================================================================
        MvcResult getRes = mockMvc.perform(get("/api/catering/bookings/" + bookingId)
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId.toString()))
                .andExpect(jsonPath("$.eventName").value("Springfield Tech Expo 2026"))
                .andExpect(jsonPath("$.guestCount").value(120))
                .andExpect(jsonPath("$.pricing.finalTotal").value(6445.53))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        // =====================================================================
        // STEP 10: Confirm Booking & Verify PostgreSQL Record & Deposit
        // =====================================================================
        ConfirmBookingRequest confirmReq = new ConfirmBookingRequest();
        confirmReq.setPaymentReference("DEP-TXN-2026-001");
        confirmReq.setNotes("Online credit card payment deposit verified");

        MvcResult confirmRes = mockMvc.perform(post("/api/catering/bookings/" + bookingId + "/confirm")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.bookingCode").isString())
                .andExpect(jsonPath("$.totalValue").value(6445.53))
                .andExpect(jsonPath("$.depositPaid").value(1933.66))
                .andExpect(jsonPath("$.nextSteps", hasSize(4)))
                .andReturn();

        BookingConfirmationResponse confirmation = objectMapper.readValue(confirmRes.getResponse().getContentAsString(), BookingConfirmationResponse.class);
        String bookingCode = confirmation.getBookingCode();
        assertThat(bookingCode).startsWith("CH-");

        // Verify status in Database
        CateringBooking dbConfirmed = bookingRepository.findById(bookingId).orElseThrow();
        assertThat(dbConfirmed.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(dbConfirmed.getBookingCode()).isEqualTo(bookingCode);

        // Verify Payment in Database
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(bookingId))
                .toList();
        assertThat(payments).isNotEmpty();
        Payment depositPayment = payments.get(0);
        assertThat(depositPayment.getAmount()).isEqualByComparingTo(new BigDecimal("1933.66"));
        assertThat(depositPayment.getPaymentStatus()).isEqualTo(Payment.PaymentStatus.PENDING);

        // =====================================================================
        // STEP 11: Confirmation Certificate Endpoint
        // =====================================================================
        mockMvc.perform(get("/api/catering/bookings/" + bookingId + "/confirmation")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingCode").value(bookingCode))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.stepTitles", hasSize(4)));

        // =====================================================================
        // STEP 12: Refresh Dashboard & Verify Confirmed Booking Appears
        // =====================================================================
        MvcResult dashRes2 = mockMvc.perform(get("/api/catering/dashboard")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andReturn();

        DashboardResponse updatedDashboard = objectMapper.readValue(dashRes2.getResponse().getContentAsString(), DashboardResponse.class);
        assertThat(updatedDashboard.getUpcomingEvents()).isGreaterThanOrEqualTo(initialUpcoming + 1);
        assertThat(updatedDashboard.getTotalRevenue()).isEqualByComparingTo(initialRevenue.add(new BigDecimal("6445.53")));
        assertThat(updatedDashboard.getActiveBookings().stream()
                .anyMatch(b -> b.getId().equals(bookingId) && b.getStatus() == BookingStatus.CONFIRMED))
                .isTrue();

        // =====================================================================
        // STEP 13: Invalid Booking Validation Check
        // =====================================================================
        CreateBookingRequest invalidReq = new CreateBookingRequest();
        invalidReq.setEventName(""); // blank
        invalidReq.setGuestCount(0); // non-positive

        mockMvc.perform(post("/api/catering/bookings")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.eventName").exists())
                .andExpect(jsonPath("$.fieldErrors.guestCount").exists());
    }
}
