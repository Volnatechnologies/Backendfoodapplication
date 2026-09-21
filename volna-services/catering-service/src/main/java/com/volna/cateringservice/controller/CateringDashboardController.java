package com.volna.cateringservice.controller;

import com.volna.cateringservice.dto.CateringDashboardResponse;
import com.volna.cateringservice.service.CateringDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catering/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class CateringDashboardController {

    private final CateringDashboardService service;

    @GetMapping
    public CateringDashboardResponse getDashboard(
            @AuthenticationPrincipal String ownerId) {
        return service.getDashboard(ownerId);
    }
}
