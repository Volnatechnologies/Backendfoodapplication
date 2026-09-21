package com.volna.authservice.repository;
import com.volna.authservice.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AuthUserRepository extends JpaRepository<AuthUser,UUID>{
 Optional<AuthUser> findByEmailIgnoreCase(String email);
 boolean existsByEmailIgnoreCase(String email);
 boolean existsByPhoneNumber(String phoneNumber);
}