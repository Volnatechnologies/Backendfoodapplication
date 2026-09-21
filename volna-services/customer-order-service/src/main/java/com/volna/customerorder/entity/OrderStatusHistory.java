package com.volna.customerorder.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;
@Entity @Table(name="order_status_history")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderStatusHistory {
 @Id private UUID id;
 @Column(name="order_id",nullable=false) private UUID orderId;
 @Enumerated(EnumType.STRING) @Column(name="old_status",length=40) private OrderStatus oldStatus;
 @Enumerated(EnumType.STRING) @Column(name="new_status",nullable=false,length=40) private OrderStatus newStatus;
 @Column(length=500) private String reason;
 @Column(name="changed_by",length=100) private String changedBy;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
 @PrePersist void create(){if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=OffsetDateTime.now(ZoneOffset.UTC);}
}
