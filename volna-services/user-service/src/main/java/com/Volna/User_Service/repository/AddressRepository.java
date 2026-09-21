package com.Volna.User_Service.repository;

import com.Volna.User_Service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserProfileId(UUID userProfileId);

    Optional<Address> findByIdAndUserProfileId(
            UUID id,
            UUID userProfileId
    );

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.isDefault = false
            WHERE a.userProfile.id = :userProfileId
            """)
    void resetDefaultAddresses(
            @Param("userProfileId") UUID userProfileId
    );
}