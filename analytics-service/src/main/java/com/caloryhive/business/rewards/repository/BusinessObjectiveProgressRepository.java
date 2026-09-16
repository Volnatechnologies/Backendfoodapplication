package com.caloryhive.business.rewards.repository;

import com.caloryhive.business.rewards.entity.BusinessObjectiveProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessObjectiveProgressRepository extends JpaRepository<BusinessObjectiveProgress, UUID> {
    List<BusinessObjectiveProgress> findByBusinessId(UUID businessId);
    Optional<BusinessObjectiveProgress> findByBusinessIdAndObjectiveId(UUID businessId, UUID objectiveId);
}
