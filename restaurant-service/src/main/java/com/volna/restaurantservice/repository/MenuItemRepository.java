package com.volna.restaurantservice.repository;

import com.volna.restaurantservice.entity.MenuItem;
import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {

    List<MenuItem> findByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category"})
    Optional<MenuItem> findById(Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findByStatus(StockStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findByIsVisible(Boolean isVisible, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findByIsAvailable(Boolean isAvailable, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<MenuItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    @Query(
        value = "SELECT DISTINCT m FROM MenuItem m JOIN FETCH m.category c WHERE " +
                "(CAST(:query AS string) IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))) AND " +
                "(:categoryId IS NULL OR c.id = :categoryId) AND " +
                "(:status IS NULL OR m.status = :status) AND " +
                "(:isVisible IS NULL OR m.isVisible = :isVisible) AND " +
                "(:isAvailable IS NULL OR m.isAvailable = :isAvailable) AND " +
                "(:spicyLevel IS NULL OR m.spicyLevel = :spicyLevel)",
        countQuery = "SELECT COUNT(m) FROM MenuItem m WHERE " +
                     "(CAST(:query AS string) IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))) AND " +
                     "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
                     "(:status IS NULL OR m.status = :status) AND " +
                     "(:isVisible IS NULL OR m.isVisible = :isVisible) AND " +
                     "(:isAvailable IS NULL OR m.isAvailable = :isAvailable) AND " +
                     "(:spicyLevel IS NULL OR m.spicyLevel = :spicyLevel)"
    )
    Page<MenuItem> filterMenuItems(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("status") StockStatus status,
            @Param("isVisible") Boolean isVisible,
            @Param("isAvailable") Boolean isAvailable,
            @Param("spicyLevel") SpicyLevel spicyLevel,
            Pageable pageable
    );
}
