package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.menu.request.*;
import com.volna.restaurantservice.dto.menu.response.ApiResponseDTO;
import com.volna.restaurantservice.dto.menu.response.MenuItemResponse;
import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import com.volna.restaurantservice.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/restaurants/menu", "/api/menu"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<MenuItemResponse>>> getMenuItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false, name = "visible") Boolean visible,
            @RequestParam(required = false, name = "available") Boolean available,
            @RequestParam(required = false) SpicyLevel spicyLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        Page<MenuItemResponse> pageData = menuItemService.getMenuItemsPaginated(search, categoryId, visible, available, status, spicyLevel, pageable);
        return ResponseEntity.ok(ApiResponseDTO.paginatedSuccess(pageData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> getMenuItemById(@PathVariable Long id) {
        MenuItemResponse item = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(item));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> createMenuItem(
            @Valid @RequestPart("item") MenuItemCreateRequest requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        MenuItemResponse createdItem = menuItemService.createMenuItem(requestDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(createdItem));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestPart("item") MenuItemUpdateRequest requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        MenuItemResponse updatedItem = menuItemService.updateMenuItem(id, requestDTO, imageFile);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest requestDTO) {
        MenuItemResponse updatedItem = menuItemService.updateStatus(id, requestDTO.getStatus());
        return ResponseEntity.ok(ApiResponseDTO.success(updatedItem));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest requestDTO) {
        MenuItemResponse updatedItem = menuItemService.updateStock(id, requestDTO.getStockQuantity());
        return ResponseEntity.ok(ApiResponseDTO.success(updatedItem));
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> updateVisibility(
            @PathVariable Long id,
            @Valid @RequestBody VisibilityUpdateRequestDTO requestDTO) {
        MenuItemResponse updatedItem = menuItemService.updateVisibility(id, requestDTO.getIsVisible());
        return ResponseEntity.ok(ApiResponseDTO.success(updatedItem));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponseDTO<MenuItemResponse>> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        MenuItemResponse updatedItem = menuItemService.updateAvailability(id, available);
        return ResponseEntity.ok(ApiResponseDTO.success(updatedItem));
    }
}
