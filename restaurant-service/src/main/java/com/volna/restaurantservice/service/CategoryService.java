package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.menu.request.MenuCategoryCreateRequest;
import com.volna.restaurantservice.dto.menu.request.MenuCategoryUpdateRequest;
import com.volna.restaurantservice.dto.menu.response.MenuCategoryResponse;
import com.volna.restaurantservice.dto.menu.response.PageResponseDTO;
import com.volna.restaurantservice.entity.MenuCategory;
import com.volna.restaurantservice.exception.DuplicateResourceException;
import com.volna.restaurantservice.exception.ResourceNotFoundException;
import com.volna.restaurantservice.mapper.MenuCategoryMapper;
import com.volna.restaurantservice.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuCategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getAllActiveCategories() {
        log.info("Fetching all active menu categories");
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<MenuCategoryResponse> searchCategories(String query, Pageable pageable) {
        log.info("Searching categories with query: {}", query);
        Page<MenuCategory> page;
        if (query == null || query.trim().isEmpty()) {
            page = categoryRepository.findAll(pageable);
        } else {
            page = categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
        }
        return PageResponseDTO.from(page.map(categoryMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public MenuCategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public MenuCategoryResponse createCategory(MenuCategoryCreateRequest requestDTO) {
        log.info("Creating category: {}", requestDTO.getName());
        if (categoryRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + requestDTO.getName());
        }
        MenuCategory category = categoryMapper.toEntity(requestDTO);
        MenuCategory savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional
    public MenuCategoryResponse updateCategory(Long id, MenuCategoryUpdateRequest requestDTO) {
        log.info("Updating category with ID: {}", id);
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException("Category already exists with name: " + requestDTO.getName());
        }

        categoryMapper.updateEntityFromDTO(requestDTO, category);
        MenuCategory updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public MenuCategoryResponse setCategoryStatus(Long id, boolean active) {
        log.info("Setting category status id: {}, active: {}", id, active);
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setIsActive(active);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category id: {}", id);
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
