package com.volna.inventory_service.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
@Component
public class JwtTokenProvider {
 private final SecretKey key;
 public JwtTokenProvider(@Value("${app.jwt.secret}") String secret){ key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
 public Claims claims(String token){ return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
 public boolean valid(String token){ try{claims(token);return true;}catch(Exception e){return false;} }
 public UUID userId(String token){ return UUID.fromString(claims(token).getSubject()); }
}
