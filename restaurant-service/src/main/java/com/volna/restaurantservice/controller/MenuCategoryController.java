package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.menu.request.MenuCategoryCreateRequest;
import com.volna.restaurantservice.dto.menu.request.MenuCategoryUpdateRequest;
import com.volna.restaurantservice.dto.menu.response.ApiResponseDTO;
import com.volna.restaurantservice.dto.menu.response.MenuCategoryResponse;
import com.volna.restaurantservice.dto.menu.response.PageResponseDTO;
import com.volna.restaurantservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/restaurants/menu/categories", "/api/menu/categories"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuCategoryResponse>>> getAllActiveCategories() {
        List<MenuCategoryResponse> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(ApiResponseDTO.success(categories));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<MenuCategoryResponse>>> searchCategories(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder,asc") String[] sort) {

        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        PageResponseDTO<MenuCategoryResponse> categories = categoryService.searchCategories(query, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuCategoryResponse>> getCategoryById(@PathVariable Long id) {
        MenuCategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(category));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MenuCategoryResponse>> createCategory(@Valid @RequestBody MenuCategoryCreateRequest requestDTO) {
        MenuCategoryResponse createdCategory = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(createdCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryUpdateRequest requestDTO) {
        MenuCategoryResponse updatedCategory = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedCategory));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<MenuCategoryResponse>> setCategoryStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        MenuCategoryResponse updatedCategory = categoryService.setCategoryStatus(id, active);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
