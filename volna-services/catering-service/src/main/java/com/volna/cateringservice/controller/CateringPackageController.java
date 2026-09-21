package com.volna.cateringservice.controller;

import com.volna.cateringservice.dto.CateringPackageRequest;
import com.volna.cateringservice.dto.CateringPackageResponse;
import com.volna.cateringservice.service.CateringPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catering/packages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class CateringPackageController {

    private final CateringPackageService service;

    @GetMapping
    public List<CateringPackageResponse> getAll(
            @AuthenticationPrincipal String ownerId) {
        return service.getPackages(ownerId);
    }

    @GetMapping("/{id}")
    public CateringPackageResponse get(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        return service.toResponse(service.getOwnedEntity(id, ownerId));
    }

    @PostMapping
    public CateringPackageResponse create(
            @AuthenticationPrincipal String ownerId,
            @Valid @RequestBody CateringPackageRequest request) {
        return service.create(ownerId, request);
    }

    @PutMapping("/{id}")
    public CateringPackageResponse update(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id,
            @Valid @RequestBody CateringPackageRequest request) {
        return service.update(ownerId, id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal String ownerId,
            @PathVariable Long id) {
        service.delete(ownerId, id);
    }
}
