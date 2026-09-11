package com.caloryhive.business.catering.controller;

import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.service.CateringService;
import com.caloryhive.business.common.enums.BookingStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CateringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CateringService cateringService;

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void shouldReturnDashboardMetrics() throws Exception {
        DashboardResponse dashboard = DashboardResponse.builder()
                .upcomingEvents(12L)
                .totalRevenue(new BigDecimal("35000.00"))
                .newInquiries(5L)
                .activeBookings(List.of())
                .recentInquiries(List.of())
                .menuPackages(List.of())
                .build();

        when(cateringService.getDashboardStatistics()).thenReturn(dashboard);

        mockMvc.perform(get("/api/catering/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upcomingEvents").value(12))
                .andExpect(jsonPath("$.totalRevenue").value(35000.00))
                .andExpect(jsonPath("$.newInquiries").value(5));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldCreateBookingSuccessfullyWith201Created() throws Exception {
        UUID bookingId = UUID.randomUUID();
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventName("Corporate Gala Dinner");
        request.setEventTypeId(UUID.randomUUID());
        request.setEventDate(LocalDate.now().plusWeeks(2));
        request.setEventTime(LocalTime.of(19, 0));
        request.setGuestCount(50);
        request.setVenueAddress("742 Evergreen Terrace");
        request.setMenuPackageId(UUID.randomUUID());

        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .id(bookingId)
                .bookingCode("CH-8821")
                .eventName("Corporate Gala Dinner")
                .guestCount(50)
                .status(BookingStatus.PENDING)
                .build();

        when(cateringService.createBookingDetails(any(CreateBookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/catering/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId.toString()))
                .andExpect(jsonPath("$.bookingCode").value("CH-8821"))
                .andExpect(jsonPath("$.eventName").value("Corporate Gala Dinner"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldSaveBookingDraftSuccessfullyWith201Created() throws Exception {
        UUID draftId = UUID.randomUUID();
        DraftBookingRequest request = DraftBookingRequest.builder()
                .eventName("Draft Holiday Party")
                .guestCount(30)
                .build();

        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .id(draftId)
                .bookingCode("CH-1024")
                .eventName("Draft Holiday Party")
                .guestCount(30)
                .status(BookingStatus.DRAFT)
                .build();

        when(cateringService.saveBookingAsDraft(any(DraftBookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/catering/bookings/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(draftId.toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetBookingDetails() throws Exception {
        UUID bookingId = UUID.randomUUID();
        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .id(bookingId)
                .bookingCode("CH-8821")
                .eventName("Tech Summit Lunch")
                .guestCount(40)
                .status(BookingStatus.PENDING)
                .build();

        when(cateringService.getBookingDetails(bookingId)).thenReturn(response);

        mockMvc.perform(get("/api/catering/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId.toString()))
                .andExpect(jsonPath("$.bookingCode").value("CH-8821"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldConfirmBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .paymentReference("PAY-REF-1234")
                .notes("Payment completed")
                .build();

        BookingConfirmationResponse response = BookingConfirmationResponse.builder()
                .bookingId(bookingId)
                .bookingCode("CH-8821")
                .eventName("Confirmed Summit Dinner")
                .totalValue(new BigDecimal("6132.36"))
                .depositPaid(new BigDecimal("1839.71"))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(cateringService.confirmBooking(eq(bookingId), any(ConfirmBookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/catering/bookings/{id}/confirm", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId.toString()))
                .andExpect(jsonPath("$.bookingCode").value("CH-8821"))
                .andExpect(jsonPath("$.totalValue").value(6132.36))
                .andExpect(jsonPath("$.depositPaid").value(1839.71))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetActiveBookings() throws Exception {
        BookingListItemResponse item = BookingListItemResponse.builder()
                .id(UUID.randomUUID())
                .bookingCode("CH-7788")
                .eventName("Annual Meet")
                .guestCount(75)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(cateringService.getActiveBookings(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/api/catering/bookings/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookingCode").value("CH-7788"))
                .andExpect(jsonPath("$.content[0].status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetUpcomingEvents() throws Exception {
        BookingListItemResponse item = BookingListItemResponse.builder()
                .id(UUID.randomUUID())
                .bookingCode("CH-9900")
                .eventName("Product Launch")
                .guestCount(100)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(cateringService.getUpcomingEvents(any(Pageable.class)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/catering/bookings/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingCode").value("CH-9900"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetPackages() throws Exception {
        MenuPackageResponse pkg = MenuPackageResponse.builder()
                .id(UUID.randomUUID())
                .name("Corporate Bites")
                .pricePerGuest(new BigDecimal("45.00"))
                .active(true)
                .build();

        when(cateringService.getCateringPackages()).thenReturn(List.of(pkg));

        mockMvc.perform(get("/api/catering/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Corporate Bites"))
                .andExpect(jsonPath("$[0].pricePerGuest").value(45.00));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetBookingFinancialSummary() throws Exception {
        UUID bookingId = UUID.randomUUID();
        FinancialSummaryResponse summary = FinancialSummaryResponse.builder()
                .guestCount(50)
                .basePrice(new BigDecimal("4500.00"))
                .customOptionsTotal(new BigDecimal("650.00"))
                .subtotal(new BigDecimal("5150.00"))
                .serviceFee(new BigDecimal("515.00"))
                .tax(new BigDecimal("467.36"))
                .finalTotal(new BigDecimal("6132.36"))
                .depositRequired(new BigDecimal("1839.71"))
                .build();

        when(cateringService.getBookingFinancialSummary(bookingId)).thenReturn(summary);

        mockMvc.perform(get("/api/catering/bookings/{id}/financial-summary", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(5150.00))
                .andExpect(jsonPath("$.finalTotal").value(6132.36))
                .andExpect(jsonPath("$.depositRequired").value(1839.71));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldGetBookingConfirmation() throws Exception {
        UUID bookingId = UUID.randomUUID();
        BookingConfirmationResponse confirmation = BookingConfirmationResponse.builder()
                .bookingId(bookingId)
                .bookingCode("CH-8821")
                .eventName("Verified Dinner")
                .totalValue(new BigDecimal("6132.36"))
                .depositPaid(new BigDecimal("1839.71"))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(cateringService.getBookingConfirmationDetails(bookingId)).thenReturn(confirmation);

        mockMvc.perform(get("/api/catering/bookings/{id}/confirmation", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingCode").value("CH-8821"))
                .andExpect(jsonPath("$.totalValue").value(6132.36));
    }

    @Test
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/catering/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldReturn400WhenGuestCountIsZero() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventName("Invalid Event");
        request.setGuestCount(0); // Invalid <= 0

        mockMvc.perform(post("/api/catering/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
