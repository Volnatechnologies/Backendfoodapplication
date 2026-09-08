package com.volna.mealservice.controller;

import com.volna.mealservice.dto.*;
import com.volna.mealservice.service.MealPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/meals/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class MealPlanController {
    private final MealPlanService service;
    private UUID owner(Authentication a){ return UUID.fromString(a.getName()); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MealPlanResponse create(Authentication a,@Valid @RequestBody CreateMealPlanRequest r){ return service.create(owner(a),r); }

    @GetMapping public List<MealPlanResponse> list(Authentication a){ return service.list(owner(a)); }

    @GetMapping("/{planId}")
    public MealPlanResponse get(Authentication a,@PathVariable UUID planId){ return service.get(owner(a),planId); }

    @PutMapping("/{planId}")
    public MealPlanResponse update(Authentication a,@PathVariable UUID planId,@Valid @RequestBody UpdateMealPlanRequest r){
        return service.update(owner(a),planId,r);
    }

    @PostMapping("/{planId}/publish")
    public MealPlanResponse publish(Authentication a,@PathVariable UUID planId){ return service.publish(owner(a),planId); }

    @PostMapping("/{planId}/pause")
    public MealPlanResponse pause(Authentication a,@PathVariable UUID planId){ return service.pause(owner(a),planId); }

    @PostMapping("/{planId}/resume")
    public MealPlanResponse resume(Authentication a,@PathVariable UUID planId){ return service.resume(owner(a),planId); }

    @DeleteMapping("/{planId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(Authentication a,@PathVariable UUID planId){ service.archive(owner(a),planId); }

    @PostMapping("/{planId}/items") @ResponseStatus(HttpStatus.CREATED)
    public MealPlanItemResponse addItem(Authentication a,@PathVariable UUID planId,@Valid @RequestBody MealPlanItemRequest r){
        return service.addItem(owner(a),planId,r);
    }

    @DeleteMapping("/{planId}/items/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(Authentication a,@PathVariable UUID planId,@PathVariable UUID itemId){
        service.removeItem(owner(a),planId,itemId);
    }
}
