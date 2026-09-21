package com.volna.cateringservice.service;

import com.volna.cateringservice.dto.*;
import com.volna.cateringservice.entity.CateringInquiry;
import com.volna.cateringservice.entity.CateringInquiryStatus;
import com.volna.cateringservice.exception.ResourceNotFoundException;
import com.volna.cateringservice.repository.CateringInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CateringInquiryService {

    private final CateringInquiryRepository repository;

    @Transactional(readOnly = true)
    public List<CateringInquiryResponse> getAll(String ownerId) {
        return repository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CateringInquiryResponse get(String ownerId, Long id) {
        return toResponse(getOwnedEntity(ownerId, id));
    }

    public CateringInquiryResponse create(String ownerId, CateringInquiryRequest request) {
        CateringInquiry inquiry = CateringInquiry.builder()
                .ownerId(ownerId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .eventType(request.getEventType())
                .eventDate(request.getEventDate())
                .guestCount(request.getGuestCount())
                .message(request.getMessage())
                .status(CateringInquiryStatus.NEW)
                .build();

        return toResponse(repository.save(inquiry));
    }

    public CateringInquiryResponse updateStatus(
            String ownerId, Long id, UpdateInquiryStatusRequest request) {

        CateringInquiry inquiry = getOwnedEntity(ownerId, id);
        inquiry.setStatus(request.getStatus());
        return toResponse(repository.save(inquiry));
    }

    private CateringInquiry getOwnedEntity(String ownerId, Long id) {
        CateringInquiry inquiry = repository.findByIdAndOwnerId(id, ownerId);
        if (inquiry == null) {
            throw new ResourceNotFoundException("Catering inquiry not found");
        }
        return inquiry;
    }

    public CateringInquiryResponse toResponse(CateringInquiry inquiry) {
        return CateringInquiryResponse.builder()
                .id(inquiry.getId())
                .name(inquiry.getName())
                .email(inquiry.getEmail())
                .phone(inquiry.getPhone())
                .eventType(inquiry.getEventType())
                .eventDate(inquiry.getEventDate())
                .guestCount(inquiry.getGuestCount())
                .message(inquiry.getMessage())
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
