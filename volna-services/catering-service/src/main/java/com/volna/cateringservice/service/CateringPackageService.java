package com.volna.cateringservice.service;

import com.volna.cateringservice.dto.CateringPackageRequest;
import com.volna.cateringservice.dto.CateringPackageResponse;
import com.volna.cateringservice.entity.CateringPackage;
import com.volna.cateringservice.exception.ResourceNotFoundException;
import com.volna.cateringservice.repository.CateringPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CateringPackageService {

    private final CateringPackageRepository repository;

    @Transactional(readOnly = true)
    public List<CateringPackageResponse> getPackages(String ownerId) {
        return repository.findByOwnerIdOrderByNameAsc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CateringPackage getOwnedEntity(Long id, String ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catering package not found"));
    }

    public CateringPackageResponse create(String ownerId, CateringPackageRequest request) {
        CateringPackage entity = CateringPackage.builder()
                .ownerId(ownerId)
                .name(request.getName())
                .description(request.getDescription())
                .pricePerGuest(request.getPricePerGuest())
                .maxGuests(request.getMaxGuests())
                .active(request.getActive() == null || request.getActive())
                .build();

        return toResponse(repository.save(entity));
    }

    public CateringPackageResponse update(
            String ownerId, Long id, CateringPackageRequest request) {

        CateringPackage entity = getOwnedEntity(id, ownerId);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPricePerGuest(request.getPricePerGuest());
        entity.setMaxGuests(request.getMaxGuests());
        entity.setActive(request.getActive() == null || request.getActive());

        return toResponse(repository.save(entity));
    }

    public void delete(String ownerId, Long id) {
        CateringPackage entity = getOwnedEntity(id, ownerId);
        repository.delete(entity);
    }

    public CateringPackageResponse toResponse(CateringPackage entity) {
        return CateringPackageResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .pricePerGuest(entity.getPricePerGuest())
                .maxGuests(entity.getMaxGuests())
                .active(entity.getActive())
                .build();
    }
}
