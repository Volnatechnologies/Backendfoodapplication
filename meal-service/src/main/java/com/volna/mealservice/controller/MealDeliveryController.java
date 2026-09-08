package com.volna.mealservice.controller;

import com.volna.mealservice.dto.DeliveryRecordResponse;
import com.volna.mealservice.dto.DeliveryStatusUpdateRequest;
import com.volna.mealservice.service.MealDeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meals/deliveries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class MealDeliveryController {

    private final MealDeliveryService service;

    private UUID owner(Authentication a) {

        return UUID.fromString(a.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryRecordResponse create(
            Authentication a,
            @RequestParam UUID planId,
            @RequestParam(required = false) UUID subscriptionId,
            @RequestParam LocalDate scheduledDate) {

        return service.create(
            owner(a), planId, subscriptionId, scheduledDate
        );
    }

    @PatchMapping("/{deliveryId}/status")
    public DeliveryRecordResponse updateStatus(
            Authentication a,
            @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryStatusUpdateRequest request) {

        return service.updateStatus(
            owner(a), deliveryId,
            request.status(), request.failureReason()
        );
    }
}
