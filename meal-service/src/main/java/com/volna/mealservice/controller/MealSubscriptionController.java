package com.volna.mealservice.controller;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.service.MealSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/meals/plans/{planId}/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class MealSubscriptionController {
    private final MealSubscriptionService service;
    private UUID owner(Authentication a){ return UUID.fromString(a.getName()); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MealSubscriptionResponse create(Authentication a,@PathVariable UUID planId,
                                           @Valid @RequestBody MealSubscriptionCreateRequest r){
        return service.create(owner(a),planId,r);
    }

    @GetMapping
    public List<MealSubscriptionResponse> list(Authentication a,@PathVariable UUID planId){
        return service.list(owner(a),planId);
    }

    @PostMapping("/{subscriptionId}/cancel")
    public MealSubscriptionResponse cancel(Authentication a,@PathVariable UUID planId,@PathVariable UUID subscriptionId,
                                           @Valid @RequestBody MealSubscriptionCancelRequest r){
        return service.cancel(owner(a),planId,subscriptionId,r);
    }
}
