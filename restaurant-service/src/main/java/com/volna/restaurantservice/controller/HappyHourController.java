package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.menu.request.HappyHourRuleRequestDTO;
import com.volna.restaurantservice.dto.menu.response.ApiResponseDTO;
import com.volna.restaurantservice.dto.menu.response.HappyHourRuleResponseDTO;
import com.volna.restaurantservice.service.HappyHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/restaurants/menu/happy-hour", "/api/v1/happy-hour", "/api/menu/happy-hour"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HappyHourController {

    private final HappyHourService happyHourService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<HappyHourRuleResponseDTO>>> getActiveRules() {
        List<HappyHourRuleResponseDTO> rules = happyHourService.getActiveRules();
        return ResponseEntity.ok(ApiResponseDTO.success(rules, "Happy hour rules retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<HappyHourRuleResponseDTO>> createRule(@Valid @RequestBody HappyHourRuleRequestDTO requestDTO) {
        HappyHourRuleResponseDTO created = happyHourService.createRule(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Happy hour rule created successfully"));
    }
}
