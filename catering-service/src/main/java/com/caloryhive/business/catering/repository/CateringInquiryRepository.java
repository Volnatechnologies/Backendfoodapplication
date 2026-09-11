package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.CateringInquiry;
import com.caloryhive.business.common.enums.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CateringInquiryRepository extends JpaRepository<CateringInquiry, UUID> {
    Page<CateringInquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<CateringInquiry> findTop5ByOrderByCreatedAtDesc();

    List<CateringInquiry> findTop10ByOrderByCreatedAtDesc();

    List<CateringInquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status);

    Page<CateringInquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status, Pageable pageable);

    long countByStatus(InquiryStatus status);
}
