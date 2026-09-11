package com.caloryhive.business.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/catering/bookings");
    }

    @Test
    void shouldHandleBookingNotFound() {
        UUID bookingId = UUID.randomUUID();
        BookingNotFoundException ex = new BookingNotFoundException(bookingId);

        ResponseEntity<ApiError> response = exceptionHandler.handleBookingNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getErrorCode()).isEqualTo("BOOKING_NOT_FOUND");
        assertThat(body.getError()).isEqualTo("BOOKING_NOT_FOUND");
        assertThat(body.getMessage()).contains(bookingId.toString());
        assertThat(body.getPath()).isEqualTo("/api/catering/bookings");
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldHandlePackageNotFound() {
        PackageNotFoundException ex = new PackageNotFoundException("Menu package not found");

        ResponseEntity<ApiError> response = exceptionHandler.handlePackageNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo("PACKAGE_NOT_FOUND");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void shouldHandleCustomOptionNotFound() {
        CustomOptionNotFoundException ex = new CustomOptionNotFoundException("Custom option not found");

        ResponseEntity<ApiError> response = exceptionHandler.handleCustomOptionNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo("CUSTOM_OPTION_NOT_FOUND");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void shouldHandleInvalidBookingStatus() {
        InvalidBookingStatusException ex = new InvalidBookingStatusException("Only draft or pending bookings can be updated");

        ResponseEntity<ApiError> response = exceptionHandler.handleInvalidBookingStatus(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_BOOKING_STATUS");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void shouldHandleInvalidStatusTransition() {
        InvalidStatusTransitionException ex = new InvalidStatusTransitionException("CONFIRMED", "DRAFT");

        ResponseEntity<ApiError> response = exceptionHandler.handleInvalidStatusTransition(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_STATUS_TRANSITION");
        assertThat(response.getBody().getMessage()).contains("CONFIRMED to DRAFT");
    }

    @Test
    void shouldHandleInvalidGuestCountFromIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Guest count must be greater than 0");

        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalStateAndArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_GUEST_COUNT");
        assertThat(response.getBody().getMessage()).isEqualTo("Guest count must be greater than 0");
    }

    @Test
    void shouldHandleInvalidDateTimeFromIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Event date cannot be in the past");

        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalStateAndArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_DATE_TIME");
    }

    @Test
    void shouldHandleInvalidPricing() {
        InvalidPricingException ex = new InvalidPricingException("Prices must never be negative");

        ResponseEntity<ApiError> response = exceptionHandler.handleInvalidPricing(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_PRICING_DATA");
    }

    @Test
    void shouldHandleDuplicateBooking() {
        DuplicateResourceException ex = new DuplicateResourceException("Booking with code CH-8821 already exists");

        ResponseEntity<ApiError> response = exceptionHandler.handleDuplicate(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo("DUPLICATE_BOOKING");
    }

    @Test
    void shouldMaskSqlAndSchemaDetailsOnDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "could not execute statement; SQL [insert into catering_bookings (id) values (?)]; constraint [uk_booking_code]");

        ResponseEntity<ApiError> response = exceptionHandler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo("DATA_INTEGRITY_ERROR");
        // Ensure SQL query and constraint name are masked
        assertThat(response.getBody().getMessage()).doesNotContain("SQL");
        assertThat(response.getBody().getMessage()).doesNotContain("uk_booking_code");
        assertThat(response.getBody().getMessage()).isEqualTo("Database constraint violation or duplicate data entry");
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("bookingRequest", "eventName", "Event name is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiError> response = exceptionHandler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Event name is required");
        assertThat(response.getBody().getFieldErrors()).containsEntry("eventName", "Event name is required");
    }

    @Test
    void shouldHandleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ApiError> response = exceptionHandler.handleBadCredentials(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode()).isEqualTo("AUTHENTICATION_ERROR");
    }

    @Test
    void shouldHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");

        ResponseEntity<ApiError> response = exceptionHandler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getErrorCode()).isEqualTo("ACCESS_DENIED");
    }

    @Test
    void shouldMaskInternalDetailsOnUnexpectedServerError() {
        NullPointerException ex = new NullPointerException("Null reference at com.caloryhive.InternalClass.method:42");

        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).doesNotContain("NullPointerException");
        assertThat(response.getBody().getMessage()).doesNotContain("com.caloryhive.InternalClass");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected server error occurred. Please contact support.");
    }
}
