package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.CateringCustomOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CateringCustomOptionRepository extends JpaRepository<CateringCustomOption, UUID> {
    List<CateringCustomOption> findByActiveTrueOrderByNameAsc();

    List<CateringCustomOption> findByActiveTrueOrderByPriceAsc();

    List<CateringCustomOption> findByIdInAndActiveTrue(Collection<UUID> ids);

    Optional<CateringCustomOption> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
