package com.volna.restaurantservice.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets;
@Component
public class JwtTokenProvider {
 private final SecretKey key;
 public JwtTokenProvider(@Value("${app.jwt.secret}") String secret){
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
 }
 public Claims parse(String token){
  return Jwts
          .parser()
          .verifyWith(key).
          build().
          parseSignedClaims(token).
          getPayload();
 }
}