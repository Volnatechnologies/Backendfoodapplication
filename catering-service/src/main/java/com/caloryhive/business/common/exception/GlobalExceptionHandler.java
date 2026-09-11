package com.caloryhive.business.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // RESOURCE NOT FOUND HANDLERS
    // =========================================================================

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> handleBookingNotFound(BookingNotFoundException ex, HttpServletRequest request) {
        log.warn("Booking not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<ApiError> handlePackageNotFound(PackageNotFoundException ex, HttpServletRequest request) {
        log.warn("Package not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "PACKAGE_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(CustomOptionNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomOptionNotFound(CustomOptionNotFoundException ex, HttpServletRequest request) {
        log.warn("Custom option not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "CUSTOM_OPTION_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        String message = ex.getMessage();
        String errorCode = "RESOURCE_NOT_FOUND";
        if (message != null) {
            String lower = message.toLowerCase();
            if (lower.contains("booking")) {
                errorCode = "BOOKING_NOT_FOUND";
            } else if (lower.contains("package")) {
                errorCode = "PACKAGE_NOT_FOUND";
            } else if (lower.contains("custom option")) {
                errorCode = "CUSTOM_OPTION_NOT_FOUND";
            }
        }
        return build(HttpStatus.NOT_FOUND, errorCode, message, request, null);
    }

    // =========================================================================
    // BOOKING STATUS & TRANSITION ERRORS
    // =========================================================================

    @ExceptionHandler(InvalidBookingStatusException.class)
    public ResponseEntity<ApiError> handleInvalidBookingStatus(InvalidBookingStatusException ex, HttpServletRequest request) {
        log.warn("Invalid booking status: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_BOOKING_STATUS", ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidStatusTransition(InvalidStatusTransitionException ex, HttpServletRequest request) {
        log.warn("Invalid status transition: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_STATUS_TRANSITION", ex.getMessage(), request, null);
    }

    // =========================================================================
    // PRICING & DATA VALIDATION ERRORS
    // =========================================================================

    @ExceptionHandler(InvalidPricingException.class)
    public ResponseEntity<ApiError> handleInvalidPricing(InvalidPricingException ex, HttpServletRequest request) {
        log.warn("Invalid pricing data: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_PRICING_DATA", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        String errorCode = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("booking")
                ? "DUPLICATE_BOOKING"
                : "DUPLICATE_RESOURCE";
        return build(HttpStatus.CONFLICT, errorCode, ex.getMessage(), request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Database data integrity violation: {}", ex.getMessage());
        // Mask SQL query and database schema details from client
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_ERROR",
                "Database constraint violation or duplicate data entry", request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String primaryMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        log.warn("Method argument validation failed: {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", primaryMessage, request, fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}", message);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request, null);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "Malformed JSON request or invalid field format", request, null);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch for parameter [{}]: {}", ex.getName(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Invalid parameter format: " + ex.getName(), request, null);
    }

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiError> handleIllegalStateAndArgument(RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Invalid request";
        String lower = message.toLowerCase();
        String errorCode = "VALIDATION_ERROR";

        if (lower.contains("guest count")) {
            errorCode = "INVALID_GUEST_COUNT";
        } else if (lower.contains("date") || lower.contains("time") || lower.contains("past")) {
            errorCode = "INVALID_DATE_TIME";
        } else if (lower.contains("status") || lower.contains("confirm") || lower.contains("cancel") || lower.contains("draft")) {
            errorCode = "INVALID_BOOKING_STATUS";
        } else if (lower.contains("price") || lower.contains("pricing") || lower.contains("negative")) {
            errorCode = "INVALID_PRICING_DATA";
        }

        log.warn("Business logic validation error [{}]: {}", errorCode, message);
        return build(HttpStatus.BAD_REQUEST, errorCode, message, request, null);
    }

    // =========================================================================
    // SECURITY & ACCESS CONTROL ERRORS
    // =========================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_ERROR", "Invalid email or password", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied on path {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this action", request, null);
    }

    // =========================================================================
    // GENERAL UNEXPECTED SERVER ERRORS
    // =========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        // Log full stack trace internally for developers, but never expose it to clients
        log.error("Unhandled internal server error on path {}: ", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected server error occurred. Please contact support.", request, null);
    }

    // =========================================================================
    // HELPER BUILDER
    // =========================================================================

    private ResponseEntity<ApiError> build(HttpStatus status, String errorCode, String message,
                                           HttpServletRequest request, Map<String, String> fieldErrors) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .status(status.value())
                .error(errorCode)
                .errorCode(errorCode)
                .message(message)
                .path(request != null ? request.getRequestURI() : "")
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors != null && !fieldErrors.isEmpty() ? fieldErrors : null)
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
