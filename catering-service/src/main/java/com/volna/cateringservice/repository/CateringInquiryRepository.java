package com.volna.cateringservice.repository;

import com.volna.cateringservice.entity.CateringInquiry;
import com.volna.cateringservice.entity.CateringInquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CateringInquiryRepository extends JpaRepository<CateringInquiry, Long> {

    List<CateringInquiry> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    List<CateringInquiry> findTop5ByOwnerIdOrderByCreatedAtDesc(String ownerId);

    long countByOwnerIdAndStatus(String ownerId, CateringInquiryStatus status);

    CateringInquiry findByIdAndOwnerId(Long id, String ownerId);
}
