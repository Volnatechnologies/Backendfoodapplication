package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.menu.request.ComboRequestDTO;
import com.volna.restaurantservice.dto.menu.response.ApiResponseDTO;
import com.volna.restaurantservice.dto.menu.response.ComboResponseDTO;
import com.volna.restaurantservice.service.ComboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/restaurants/menu/combos", "/api/v1/combos", "/api/menu/combos"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComboController {

    private final ComboService comboService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ComboResponseDTO>>> getActiveCombos() {
        List<ComboResponseDTO> combos = comboService.getActiveCombos();
        return ResponseEntity.ok(ApiResponseDTO.success(combos, "Combos retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ComboResponseDTO>> createCombo(@Valid @RequestBody ComboRequestDTO requestDTO) {
        ComboResponseDTO created = comboService.createCombo(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Combo created successfully"));
    }
}
