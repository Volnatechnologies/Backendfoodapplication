package com.caloryhive.business.catering.entity;

import com.caloryhive.business.common.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catering_menu_packages", indexes = {
        @Index(name = "idx_menu_package_active", columnList = "active"),
        @Index(name = "idx_menu_package_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class CateringMenuPackage extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price_per_guest", nullable = false, precision = 19, scale = 4)
    private BigDecimal pricePerGuest;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
