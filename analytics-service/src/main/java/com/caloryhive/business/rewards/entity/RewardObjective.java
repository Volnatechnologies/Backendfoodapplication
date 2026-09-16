package com.caloryhive.business.rewards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reward_objectives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardObjective {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "icon_name", nullable = false, length = 50)
    @Builder.Default
    private String iconName = "Star";

    @Column(name = "points_reward", nullable = false)
    private Integer pointsReward;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "target_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
