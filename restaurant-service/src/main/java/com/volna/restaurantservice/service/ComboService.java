package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.menu.request.ComboRequestDTO;
import com.volna.restaurantservice.dto.menu.response.ComboResponseDTO;
import com.volna.restaurantservice.entity.Combo;
import com.volna.restaurantservice.entity.MenuItem;
import com.volna.restaurantservice.mapper.MenuItemMapper;
import com.volna.restaurantservice.repository.ComboRepository;
import com.volna.restaurantservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComboService {

    private final ComboRepository comboRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Transactional(readOnly = true)
    public List<ComboResponseDTO> getActiveCombos() {
        return comboRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComboResponseDTO createCombo(ComboRequestDTO requestDTO) {
        List<MenuItem> items = menuItemRepository.findAllById(requestDTO.getItemIds());

        Combo combo = Combo.builder()
                .restaurantId(requestDTO.getRestaurantId())
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .comboPrice(requestDTO.getComboPrice())
                .items(new HashSet<>(items))
                .isActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true)
                .build();

        return mapToDTO(comboRepository.save(combo));
    }

    private ComboResponseDTO mapToDTO(Combo combo) {
        return ComboResponseDTO.builder()
                .id(combo.getId())
                .restaurantId(combo.getRestaurantId())
                .title(combo.getTitle())
                .description(combo.getDescription())
                .comboPrice(combo.getComboPrice())
                .items(combo.getItems().stream().map(menuItemMapper::toResponse).collect(Collectors.toList()))
                .isActive(combo.getIsActive())
                .build();
    }
}
