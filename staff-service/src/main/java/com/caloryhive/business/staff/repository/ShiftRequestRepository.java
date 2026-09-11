package com.caloryhive.business.staff.repository;

import com.caloryhive.business.staff.entity.ShiftRequest;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, UUID> {

    List<ShiftRequest> findByStatus(RequestStatus status);

    List<ShiftRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    List<ShiftRequest> findAllByOrderByCreatedAtDesc();

    List<ShiftRequest> findByStaffId(UUID staffId);

    List<ShiftRequest> findByRequestType(RequestType requestType);

    long countByStatus(RequestStatus status);

    @Query("SELECT r FROM ShiftRequest r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:staffId IS NULL OR r.staff.id = :staffId) AND " +
           "(:requestType IS NULL OR r.requestType = :requestType) " +
           "ORDER BY r.createdAt DESC")
    List<ShiftRequest> findWithFilters(@Param("status") RequestStatus status,
                                       @Param("staffId") UUID staffId,
                                       @Param("requestType") RequestType requestType);
}
