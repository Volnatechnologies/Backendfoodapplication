package com.volna.cateringservice.repository;

import com.volna.cateringservice.entity.CateringEvent;
import com.volna.cateringservice.entity.CateringEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CateringEventRepository extends JpaRepository<CateringEvent, Long> {

    List<CateringEvent> findByOwnerIdOrderByEventDateAscEventTimeAsc(String ownerId);

    List<CateringEvent> findTop5ByOwnerIdAndEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(
            String ownerId, LocalDate date);

    List<CateringEvent> findByOwnerIdAndStatusOrderByEventDateAscEventTimeAsc(
            String ownerId, CateringEventStatus status);

    Optional<CateringEvent> findByIdAndOwnerId(Long id, String ownerId);

    long countByOwnerIdAndEventDateGreaterThanEqual(String ownerId, LocalDate date);

    long countByOwnerIdAndStatus(String ownerId, CateringEventStatus status);

    long countByOwnerIdAndEventDateBetween(
            String ownerId, LocalDate from, LocalDate to);
}
