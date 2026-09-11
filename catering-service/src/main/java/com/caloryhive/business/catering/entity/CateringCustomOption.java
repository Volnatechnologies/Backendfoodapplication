package com.caloryhive.business.catering.entity;

import com.caloryhive.business.common.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catering_custom_options", indexes = {
        @Index(name = "idx_custom_option_active", columnList = "active"),
        @Index(name = "idx_custom_option_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class CateringCustomOption extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
