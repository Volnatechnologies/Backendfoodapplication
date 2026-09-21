package com.volna.customerorder.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name="order_items",indexes=@Index(name="idx_order_items_order",columnList="order_id"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItem {
 @Id private UUID id;
 @Column(name="order_id",nullable=false) private UUID orderId;
 @Column(name="menu_item_id",nullable=false) private UUID menuItemId;
 @Column(name="item_name_snapshot",nullable=false,length=200) private String itemNameSnapshot;
 @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
 @Column(nullable=false) private Integer quantity;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal total;
 @PrePersist void create(){if(id==null)id=UUID.randomUUID();}
}
