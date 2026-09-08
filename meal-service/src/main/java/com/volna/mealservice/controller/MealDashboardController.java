package com.volna.mealservice.controller;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.service.MealDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class MealDashboardController {
    private final MealDashboardService service;
    private UUID owner(Authentication a){ return UUID.fromString(a.getName()); }

    @GetMapping("/dashboard")
    public MealsDashboardResponse dashboard(Authentication a){ return service.dashboard(owner(a)); }

    @GetMapping("/rotation")
    public List<MenuRotationDayResponse> rotation(Authentication a,@RequestParam(defaultValue="7") int days){
        return service.rotation(owner(a),days);
    }

    @GetMapping("/subscription-updates")
    public SubscriptionUpdatesResponse updates(Authentication a){ return service.updates(owner(a)); }
}
