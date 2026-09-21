package com.volna.menuservice.security;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter)throws Exception{
  http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
     .requestMatchers("/actuator/health","/actuator/info").permitAll()
     .requestMatchers(HttpMethod.GET,"/api/v1/menu/items/**").permitAll()
     .requestMatchers(HttpMethod.POST,"/api/v1/menu/items").hasRole("RESTAURANT_OWNER")
     .requestMatchers(HttpMethod.PUT,"/api/v1/menu/items/**").hasRole("RESTAURANT_OWNER")
     .requestMatchers(HttpMethod.DELETE,"/api/v1/menu/items/**").hasRole("RESTAURANT_OWNER")
     .anyRequest().authenticated())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}
