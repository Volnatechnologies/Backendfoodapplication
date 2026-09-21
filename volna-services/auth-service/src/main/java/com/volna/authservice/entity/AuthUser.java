package com.volna.authservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
@Entity @Table(name="auth_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
 @Id
 private UUID id;
 @Column(name="full_name",nullable=false)
 private String fullName;
 @Column(nullable=false,unique=true)
 private String email;
 @Column(name="phone_number",nullable=false,unique=true)
 private String phoneNumber;
 @Column(name="password_hash",nullable=false)
 private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false)
 private Role role;
 @Column(nullable=false)
 private boolean enabled;
 @Column(name="created_at",nullable=false)
 private OffsetDateTime createdAt;
 @Column(name="updated_at",nullable=false)
 private OffsetDateTime updatedAt;
 @PrePersist void create(){
  var now=OffsetDateTime.now(ZoneOffset.UTC);
  if(id==null)
   id=UUID.randomUUID();
  createdAt=now;
  updatedAt=now;
  if(role==null)
   role=Role.RESTAURANT_OWNER;}
 @PreUpdate void update(){
  updatedAt=OffsetDateTime.now(ZoneOffset.UTC);
 }
}