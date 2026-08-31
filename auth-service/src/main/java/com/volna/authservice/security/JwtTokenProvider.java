package com.volna.authservice.security;
import com.volna.authservice.entity.AuthUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
public class JwtTokenProvider {
 private final SecretKey key;
 private final long expirationMs;
 public JwtTokenProvider(@Value("${app.jwt.secret}")
                         String secret,
                         @Value("${app.jwt.expiration-ms}")long exp){
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  expirationMs=exp;
 }
 public String generateToken(AuthUser user){
  Date now=new Date(), expiry=new Date(now.getTime()+expirationMs);
  return Jwts.builder().
          subject(user.getId().toString()).claim("roles",List.of(user.getRole().name()))
   .issuedAt(now).expiration(expiry).signWith(key).compact();
 }
 public long getExpirationMs(){
  return expirationMs;}
}