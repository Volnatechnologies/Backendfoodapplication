package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.dashboard.RestaurantDashboardResponse;
import com.volna.restaurantservice.service.RestaurantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/dashboard")
@RequiredArgsConstructor
public class RestaurantDashboardController {

    private final RestaurantDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public RestaurantDashboardResponse getDashboard(
            Authentication authentication
    ) {

        UUID ownerId = UUID.fromString(authentication.getName());

        return dashboardService.getDashboard(ownerId);
    }
}