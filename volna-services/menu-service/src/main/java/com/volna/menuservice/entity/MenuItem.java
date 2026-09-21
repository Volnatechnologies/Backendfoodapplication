package com.volna.menuservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name="menu_items", indexes={
    @Index(name="idx_menu_items_restaurant", columnList="restaurant_id"),
    @Index(name="idx_menu_items_category", columnList="category")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuItem {
    @Id private UUID id;
    @Column(name="restaurant_id", nullable=false) private UUID restaurantId;
    @Column(nullable=false, length=150) private String name;
    @Column(length=1000) private String description;
    @Column(name="image_url", length=1000) private String imageUrl;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal price;
    @Column(length=80) private String category;
    @Column(name="is_veg", nullable=false) private boolean veg;
    @Column(nullable=false) private boolean available;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt;
    @PrePersist void onCreate() {
        OffsetDateTime now=OffsetDateTime.now(ZoneOffset.UTC);
        if(id==null) id=UUID.randomUUID();
        createdAt=now; updatedAt=now;
    }
    @PreUpdate void onUpdate(){ updatedAt=OffsetDateTime.now(ZoneOffset.UTC); }
}
