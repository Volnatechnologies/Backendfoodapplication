package com.volna.restaurantservice.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="restaurant_documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDocument {
 @Id
 private UUID id;
 @Column(name="restaurant_id",nullable=false)
 private UUID restaurantId;
 @Enumerated(EnumType.STRING) @Column(nullable=false)
 private DocumentType type;
 @Column(name="file_url",nullable=false,length=1000)
 private String fileUrl;
 @Enumerated(EnumType.STRING) @Column(nullable=false)
 private DocumentStatus status;
 @Column(name="uploaded_at",nullable=false)
 private OffsetDateTime uploadedAt;
 @PrePersist void create(){
  if(id==null)id=UUID.randomUUID();
  if(uploadedAt==null)uploadedAt=OffsetDateTime.now(ZoneOffset.UTC);
  if(status==null)status=DocumentStatus.PENDING;}
}