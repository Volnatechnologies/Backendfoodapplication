package com.caloryhive.business.user.repository;

import com.caloryhive.business.common.enums.RoleName;
import com.caloryhive.business.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
}

