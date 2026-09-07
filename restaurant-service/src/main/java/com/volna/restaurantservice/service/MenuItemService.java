package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.menu.request.MenuItemCreateRequest;
import com.volna.restaurantservice.dto.menu.request.MenuItemUpdateRequest;
import com.volna.restaurantservice.dto.menu.response.MenuItemResponse;
import com.volna.restaurantservice.entity.MenuCategory;
import com.volna.restaurantservice.entity.MenuItem;
import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import com.volna.restaurantservice.exception.ResourceNotFoundException;
import com.volna.restaurantservice.mapper.MenuItemMapper;
import com.volna.restaurantservice.repository.MenuCategoryRepository;
import com.volna.restaurantservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final MenuItemMapper menuItemMapper;

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> getMenuItemsPaginated(
            String search, Long categoryId, Boolean isVisible, Boolean isAvailable, StockStatus status, SpicyLevel spicyLevel, Pageable pageable) {
        log.info("Fetching paginated menu items - search: '{}', categoryId: {}", search, categoryId);
        Page<MenuItem> menuItemPage = menuItemRepository.filterMenuItems(search, categoryId, status, isVisible, isAvailable, spicyLevel, pageable);
        return menuItemPage.map(menuItemMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsList(
            String search, Long categoryId, Boolean isVisible, Boolean isAvailable, StockStatus status, SpicyLevel spicyLevel) {
        log.info("Fetching unpaginated menu items list");
        return menuItemRepository.filterMenuItems(search, categoryId, status, isVisible, isAvailable, spicyLevel, Pageable.unpaged())
                .getContent()
                .stream()
                .map(menuItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        log.info("Fetching menu item by ID: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return menuItemMapper.toResponse(menuItem);
    }

    @Transactional
    public MenuItemResponse createMenuItem(MenuItemCreateRequest requestDTO, MultipartFile imageFile) {
        log.info("Creating menu item: {}", requestDTO.getName());

        MenuCategory category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileStorageService.storeFile(imageFile);
        }

        MenuItem menuItem = menuItemMapper.toEntity(requestDTO, category, imageUrl);
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toResponse(savedItem);
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemUpdateRequest requestDTO, MultipartFile imageFile) {
        log.info("Updating menu item id: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        MenuCategory category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        String imageUrl = menuItem.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageUrl != null) {
                fileStorageService.deleteFile(imageUrl);
            }
            imageUrl = fileStorageService.storeFile(imageFile);
        } else if (requestDTO.getImageUrl() != null && requestDTO.getImageUrl().trim().isEmpty()) {
            if (imageUrl != null) {
                fileStorageService.deleteFile(imageUrl);
            }
            imageUrl = null;
        }

        menuItemMapper.updateEntityFromDTO(requestDTO, menuItem, category, imageUrl);
        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toResponse(updatedItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Deleting menu item id: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        if (menuItem.getImageUrl() != null) {
            fileStorageService.deleteFile(menuItem.getImageUrl());
        }
        menuItemRepository.delete(menuItem);
    }

    @Transactional
    public MenuItemResponse updateStock(Long id, Integer quantity) {
        log.info("Updating stock for item id: {} to quantity: {}", id, quantity);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setStockQuantity(quantity);
        menuItem.calculateAvailabilityAndStatus();
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    public MenuItemResponse updateStatus(Long id, StockStatus status) {
        log.info("Updating status for item id: {} to status: {}", id, status);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setStatus(status);
        if (status == StockStatus.OUT_OF_STOCK) {
            menuItem.setIsAvailable(false);
        }
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    public MenuItemResponse updateVisibility(Long id, Boolean isVisible) {
        log.info("Updating visibility for item id: {} to: {}", id, isVisible);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setIsVisible(isVisible);
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    public MenuItemResponse updateAvailability(Long id, Boolean isAvailable) {
        log.info("Updating availability for item id: {} to: {}", id, isAvailable);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setIsAvailable(isAvailable);
        return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
    }
}
