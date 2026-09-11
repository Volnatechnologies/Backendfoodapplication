package com.caloryhive.business.catering.controller;

import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.service.CateringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catering")
@RequiredArgsConstructor
@Tag(name = "Catering Management", description = "REST APIs for Catering Dashboard, Bookings, Menus, Pricing, and Confirmation")
public class CateringController {

    private final CateringService cateringService;

    // =========================================================================
    // DASHBOARD
    // =========================================================================

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Catering Dashboard", description = "Retrieves KPI metrics (upcoming events count, total revenue, new inquiries), active bookings, recent inquiries, and menu packages")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(cateringService.getDashboardStatistics());
    }

    // =========================================================================
    // CATERING BOOKINGS
    // =========================================================================

    @PostMapping("/bookings")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create Catering Booking", description = "Creates a new catering booking in PENDING status, calculates dynamic pricing, and generates a CH-XXXX booking code")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid booking details or guest count <= 0"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Event type or menu package not found")
    })
    public ResponseEntity<BookingDetailsResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingDetailsResponse response = cateringService.createBookingDetails(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bookings/draft")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Save Booking as Draft", description = "Saves an in-progress booking draft without strict venue/package requirements")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Draft booking saved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<BookingDetailsResponse> saveDraftBooking(@Valid @RequestBody DraftBookingRequest request) {
        BookingDetailsResponse response = cateringService.saveBookingAsDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/bookings")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List Catering Bookings", description = "Retrieves paginated bookings with optional status, search, and event type filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<Page<BookingListItemResponse>> listBookings(
            @Parameter(description = "Filter by BookingStatus (e.g. PENDING, CONFIRMED, DRAFT, CANCELLED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Search across event name and booking code")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by event type name")
            @RequestParam(required = false) String eventType,
            Pageable pageable) {
        return ResponseEntity.ok(cateringService.listBookings(status, search, eventType, pageable));
    }

    @GetMapping("/bookings/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Active Bookings", description = "Retrieves active/confirmed bookings with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active bookings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<Page<BookingListItemResponse>> getActiveBookings(Pageable pageable) {
        return ResponseEntity.ok(cateringService.getActiveBookings(pageable));
    }

    @GetMapping("/bookings/upcoming")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Upcoming Events", description = "Retrieves upcoming non-cancelled events ordered chronologically")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upcoming events retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<List<BookingListItemResponse>> getUpcomingEvents(Pageable pageable) {
        return ResponseEntity.ok(cateringService.getUpcomingEvents(pageable));
    }

    @GetMapping("/bookings/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Booking Details", description = "Retrieves full details, menu selections, and pricing breakdown for a specific booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDetailsResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getBookingDetails(id));
    }

    @PutMapping("/bookings/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update Booking", description = "Updates details of a draft or pending booking and recalculates prices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or booking cannot be updated in its current status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to update this booking"),
            @ApiResponse(responseCode = "404", description = "Booking, event type, or package not found")
    })
    public ResponseEntity<BookingDetailsResponse> updateBooking(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookingRequest request) {
        return ResponseEntity.ok(cateringService.updateBookingDetails(id, request));
    }

    @PostMapping("/bookings/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Confirm Booking", description = "Finalizes booking, locks prices, transitions status to CONFIRMED, creates deposit payment record, and sends notification")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g. blank venue address) or booking already confirmed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to confirm this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingConfirmationResponse> confirmBooking(
            @PathVariable UUID id,
            @RequestBody(required = false) ConfirmBookingRequest request) {
        return ResponseEntity.ok(cateringService.confirmBooking(id, request));
    }

    @GetMapping("/bookings/{id}/confirmation")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Booking Confirmation", description = "Retrieves confirmed booking receipt with next onboarding steps and deposit details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Confirmation details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingConfirmationResponse> getConfirmation(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getBookingConfirmationDetails(id));
    }

    @GetMapping("/bookings/{id}/financial-summary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Financial Summary", description = "Retrieves verified financial breakdown (base price, custom options, service fee, taxes, total, deposit) for a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Financial summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<FinancialSummaryResponse> getFinancialSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getBookingFinancialSummary(id));
    }

    @PostMapping("/bookings/calculate-price")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calculate Price Preview", description = "Calculates instant pricing breakdown without saving a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price calculation computed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid guest count or package"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Package or custom option not found")
    })
    public ResponseEntity<FinancialSummaryResponse> calculatePrice(@Valid @RequestBody PriceCalculationRequest request) {
        return ResponseEntity.ok(cateringService.calculateBookingFinancialSummary(request));
    }

    @PutMapping("/bookings/{id}/custom-options")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update Custom Options", description = "Adds or removes custom menu options on an existing booking and updates total pricing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Custom options updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to modify this booking"),
            @ApiResponse(responseCode = "404", description = "Booking or custom option not found")
    })
    public ResponseEntity<BookingDetailsResponse> updateCustomOptions(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomOptionsRequest request) {
        return ResponseEntity.ok(cateringService.addRemoveCustomMenuOptions(id, request));
    }

    @PatchMapping("/bookings/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel Booking", description = "Cancels a catering booking and logs audit history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Booking is already cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to cancel this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDetailsResponse> cancelBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.cancelBookingDetails(id));
    }

    @GetMapping("/bookings/{id}/timeline")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Booking Timeline", description = "Retrieves preparation, logistics, quality assurance, and setup timeline steps")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Timeline retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<List<TimelineItemResponse>> getTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getTimeline(id));
    }

    @GetMapping("/bookings/{id}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Booking Status History", description = "Retrieves status change audit logs for a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<List<Object>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getHistory(id));
    }

    // =========================================================================
    // CATERING PACKAGES
    // =========================================================================

    @GetMapping(value = {"/packages", "/menu-packages"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List Catering Packages", description = "Retrieves all active catering packages (e.g. Corporate Bites, Premium Plated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catering packages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<List<MenuPackageResponse>> getPackages() {
        return ResponseEntity.ok(cateringService.getCateringPackages());
    }

    @GetMapping(value = {"/packages/{id}", "/menu-packages/{id}"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Catering Package", description = "Retrieves details of a specific catering package by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<MenuPackageResponse> getPackage(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getCateringPackage(id));
    }

    @PostMapping(value = {"/packages", "/menu-packages"})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Catering Package (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Package created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Object> createMenuPackage(@RequestBody Object request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cateringService.createMenuPackage(request));
    }

    @PutMapping(value = {"/packages/{id}", "/menu-packages/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Catering Package (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<Object> updateMenuPackage(@PathVariable UUID id, @RequestBody Object request) {
        return ResponseEntity.ok(cateringService.updateMenuPackage(id, request));
    }

    @DeleteMapping(value = {"/packages/{id}", "/menu-packages/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Catering Package (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Package deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<Void> deleteMenuPackage(@PathVariable UUID id) {
        cateringService.deleteMenuPackage(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // EVENT TYPES
    // =========================================================================

    @GetMapping("/event-types")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List Event Types", description = "Retrieves all available event types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event types retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<List<EventTypeResponse>> getEventTypes() {
        return ResponseEntity.ok(cateringService.getCateringEventTypes());
    }

    @PostMapping("/event-types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Event Type (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event type created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Object> createEventType(@RequestBody Object request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cateringService.createEventType(request));
    }

    @PutMapping("/event-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Event Type (Admin)")
    public ResponseEntity<Object> updateEventType(@PathVariable UUID id, @RequestBody Object request) {
        return ResponseEntity.ok(cateringService.updateEventType(id, request));
    }

    @DeleteMapping("/event-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Event Type (Admin)")
    public ResponseEntity<Void> deleteEventType(@PathVariable UUID id) {
        cateringService.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // CUSTOM OPTIONS
    // =========================================================================

    @GetMapping("/custom-options")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List Custom Options", description = "Retrieves active custom menu add-ons (Live Cooking Station, Dessert Bar, etc.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Custom options retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<List<CustomOptionResponse>> getCustomOptions() {
        return ResponseEntity.ok(cateringService.getCateringCustomOptions());
    }

    @PostMapping("/custom-options")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Custom Option (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Custom option created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Object> createCustomOption(@RequestBody Object request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cateringService.createCustomOption(request));
    }

    @PutMapping("/custom-options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Custom Option (Admin)")
    public ResponseEntity<Object> updateCustomOption(@PathVariable UUID id, @RequestBody Object request) {
        return ResponseEntity.ok(cateringService.updateCustomOption(id, request));
    }

    @DeleteMapping("/custom-options/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Custom Option (Admin)")
    public ResponseEntity<Void> deleteCustomOption(@PathVariable UUID id) {
        cateringService.deleteCustomOption(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // INQUIRIES & SEARCH
    // =========================================================================

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search Bookings and Inquiries")
    public ResponseEntity<Object> search(@RequestParam String q) {
        return ResponseEntity.ok(cateringService.search(q));
    }

    @GetMapping("/inquiries")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List Inquiries")
    public ResponseEntity<Page<Object>> getInquiries(Pageable pageable) {
        return ResponseEntity.ok(cateringService.getInquiries(pageable));
    }

    @GetMapping("/inquiries/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Inquiry by ID")
    public ResponseEntity<Object> getInquiry(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.getInquiry(id));
    }

    @PatchMapping("/inquiries/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark Inquiry as Read")
    public ResponseEntity<Object> markInquiryRead(@PathVariable UUID id) {
        return ResponseEntity.ok(cateringService.markInquiryRead(id));
    }

    @PatchMapping("/inquiries/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update Inquiry Status")
    public ResponseEntity<Object> updateInquiryStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(cateringService.updateInquiryStatus(id, status));
    }
}
