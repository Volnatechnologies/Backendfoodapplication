package com.caloryhive.business.staff.repository;

import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    Optional<Staff> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<Staff> findByStatus(StaffStatus status);

    List<Staff> findByActiveTrue();

    List<Staff> findByActiveTrueOrderByNameAsc();

    List<Staff> findByStatusAndActiveTrue(StaffStatus status);

    List<Staff> findByRoleIgnoreCaseAndActiveTrue(String role);

    List<Staff> findByStatusAndRoleIgnoreCaseAndActiveTrue(StaffStatus status, String role);

    long countByActiveTrue();

    long countByStatus(StaffStatus status);

    long countByStatusInAndActiveTrue(Collection<StaffStatus> statuses);

    @Query("SELECT s FROM Staff s WHERE s.active = true " +
           "AND (:query IS NULL OR :query = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.role) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:role IS NULL OR :role = '' OR LOWER(s.role) = LOWER(:role))")
    Page<Staff> searchStaff(@Param("query") String query,
                            @Param("status") StaffStatus status,
                            @Param("role") String role,
                            Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE s.active = true AND (" +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.role) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "(s.email IS NOT NULL AND LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(s.stationArea IS NOT NULL AND LOWER(s.stationArea) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "ORDER BY s.name ASC")
    List<Staff> searchByKeyword(@Param("keyword") String keyword);
}
