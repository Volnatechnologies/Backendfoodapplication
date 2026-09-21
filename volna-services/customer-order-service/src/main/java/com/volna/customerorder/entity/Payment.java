package com.volna.customerorder.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
@Entity @Table(name="payments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {
 @Id private UUID id;
 @Column(name="order_id",nullable=false,unique=true) private UUID orderId;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal amount;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private PaymentStatus status;
 @Column(name="provider_reference",length=200) private String providerReference;
 @Column(name="idempotency_key",length=100) private String idempotencyKey;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
 @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
 @PrePersist void create(){OffsetDateTime n=OffsetDateTime.now(ZoneOffset.UTC);if(id==null)id=UUID.randomUUID();createdAt=n;updatedAt=n;}
 @PreUpdate void update(){updatedAt=OffsetDateTime.now(ZoneOffset.UTC);}
}
