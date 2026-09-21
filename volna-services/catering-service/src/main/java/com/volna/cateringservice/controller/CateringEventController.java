package com.volna.cateringservice.controller;

import com.volna.cateringservice.dto.*;
import com.volna.cateringservice.service.CateringEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catering/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class CateringEventController {

    private final CateringEventService service;

    @GetMapping
    public List<CateringEventResponse> getAll(
            @AuthenticationPrincipal String ownerId) {
        return service.getEvents(ownerId);
    }

    @GetMapping("/{id}")
    public CateringEventResponse get(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        return service.get(ownerId, id);
    }

    @PostMapping
    public CateringEventResponse create(
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody CateringEventRequest request) {
        return service.create(ownerId, request);
    }

    @PutMapping("/{id}")
    public CateringEventResponse update(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id,
            @Valid @RequestBody CateringEventRequest request) {
        return service.update(ownerId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        service.delete(ownerId, id);
    }

    @PostMapping("/{id}/confirm")
    public ConfirmBookingResponse confirm(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        return service.confirm(ownerId, id);
    }
}
