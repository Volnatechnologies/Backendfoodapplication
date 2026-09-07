package com.volna.restaurantservice.repository;

import com.volna.restaurantservice.entity.HappyHourRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HappyHourRepository extends JpaRepository<HappyHourRule, Long> {
    List<HappyHourRule> findByIsActiveTrue();
}
