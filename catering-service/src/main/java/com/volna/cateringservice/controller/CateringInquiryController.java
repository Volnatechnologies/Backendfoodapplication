package com.volna.cateringservice.controller;

import com.volna.cateringservice.dto.*;
import com.volna.cateringservice.service.CateringInquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catering/inquiries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class CateringInquiryController {

    private final CateringInquiryService service;

    @GetMapping
    public List<CateringInquiryResponse> getAll(
            @AuthenticationPrincipal String ownerId) {
        return service.getAll(ownerId);
    }

    @GetMapping("/{id}")
    public CateringInquiryResponse get(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        return service.get(ownerId, id);
    }

    @PostMapping
    public CateringInquiryResponse create(
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody CateringInquiryRequest request) {
        return service.create(ownerId, request);
    }

    @PatchMapping("/{id}/status")
    public CateringInquiryResponse updateStatus(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateInquiryStatusRequest request) {
        return service.updateStatus(ownerId, id, request);
    }
}
