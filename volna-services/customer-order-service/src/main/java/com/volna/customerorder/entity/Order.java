package com.volna.customerorder.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
@Entity @Table(name="orders",indexes={
 @Index(name="idx_orders_customer",columnList="customer_id"),
 @Index(name="idx_orders_restaurant",columnList="restaurant_id")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
 @Id private UUID id;
 @Column(name="customer_id",nullable=false) private UUID customerId;
 @Column(name="restaurant_id",nullable=false) private UUID restaurantId;
 @Column(name="address_id",nullable=false) private UUID addressId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private OrderStatus status;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal tax;
 @Column(name="delivery_fee",nullable=false,precision=12,scale=2) private BigDecimal deliveryFee;
 @Column(name="packaging_fee",nullable=false,precision=12,scale=2) private BigDecimal packagingFee;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal discount;
 @Column(name="total_amount",nullable=false,precision=12,scale=2) private BigDecimal totalAmount;
 @Enumerated(EnumType.STRING) @Column(name="payment_status",nullable=false,length=20) private PaymentStatus paymentStatus;
 @Column(name="idempotency_key",unique=true,length=100) private String idempotencyKey;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt;
 @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
 @PrePersist void create(){OffsetDateTime n=OffsetDateTime.now(ZoneOffset.UTC);if(id==null)id=UUID.randomUUID();createdAt=n;updatedAt=n;}
 @PreUpdate void update(){updatedAt=OffsetDateTime.now(ZoneOffset.UTC);}
}
