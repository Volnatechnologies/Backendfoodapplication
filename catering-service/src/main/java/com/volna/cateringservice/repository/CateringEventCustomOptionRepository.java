package com.volna.cateringservice.repository;

import com.volna.cateringservice.entity.CateringEventCustomOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CateringEventCustomOptionRepository
        extends JpaRepository<CateringEventCustomOption, Long> {

    List<CateringEventCustomOption> findByEventIdOrderByIdAsc(Long eventId);

    void deleteByEventId(Long eventId);
}
