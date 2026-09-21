package com.volna.restaurantservice.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.*; import java.util.UUID;
@Entity @Table(name="restaurants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
 @Id
 private UUID id;
 @Column(name="owner_id",nullable=false,unique=true)
 private UUID ownerId;
 @Column(nullable=false,length=150)
 private String name;
 @Column(length=1000)
 private String description;
 @Column(nullable=false,length=20)
 private String phone;
 @Column(nullable=false,unique=true)
 private String email;
 @Column(nullable=false,length=500)
 private String address;
 private BigDecimal latitude;
 private BigDecimal longitude;
 @Column(name="opening_time")
 private LocalTime openingTime;
 @Column(name="closing_time")
 private LocalTime closingTime;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30)
 private RestaurantStatus status;
 @Column(name="created_at",nullable=false)
 private OffsetDateTime createdAt;
 @Column(name="updated_at",nullable=false)
 private OffsetDateTime updatedAt;
 @PrePersist void create(){
  var n=OffsetDateTime.now(ZoneOffset.UTC);
  if(id==null)id=UUID.randomUUID();
  createdAt=n;
  updatedAt=n;
  if(status==null)
   status=RestaurantStatus.PENDING;}
 @PreUpdate void update(){
  updatedAt=OffsetDateTime.now(ZoneOffset.UTC);
 }
}