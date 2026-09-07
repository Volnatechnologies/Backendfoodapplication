package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.menu.request.HappyHourRuleRequestDTO;
import com.volna.restaurantservice.dto.menu.response.HappyHourRuleResponseDTO;
import com.volna.restaurantservice.entity.HappyHourRule;
import com.volna.restaurantservice.repository.HappyHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HappyHourService {

    private final HappyHourRepository happyHourRepository;

    @Transactional(readOnly = true)
    public List<HappyHourRuleResponseDTO> getActiveRules() {
        return happyHourRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HappyHourRuleResponseDTO createRule(HappyHourRuleRequestDTO requestDTO) {
        HappyHourRule rule = HappyHourRule.builder()
                .restaurantId(requestDTO.getRestaurantId())
                .title(requestDTO.getTitle())
                .discountPercentage(requestDTO.getDiscountPercentage())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .isActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true)
                .build();

        return mapToDTO(happyHourRepository.save(rule));
    }

    private HappyHourRuleResponseDTO mapToDTO(HappyHourRule rule) {
        return HappyHourRuleResponseDTO.builder()
                .id(rule.getId())
                .restaurantId(rule.getRestaurantId())
                .title(rule.getTitle())
                .discountPercentage(rule.getDiscountPercentage())
                .startTime(rule.getStartTime())
                .endTime(rule.getEndTime())
                .isActive(rule.getIsActive())
                .build();
    }
}
