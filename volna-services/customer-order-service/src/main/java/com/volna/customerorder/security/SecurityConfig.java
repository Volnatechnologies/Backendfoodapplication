package com.volna.customerorder.security;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter filter)throws Exception{
  http.csrf(AbstractHttpConfigurer::disable).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->
           a.requestMatchers(
                           "/actuator/health/**",
                           "/actuator/info",
                           "/v3/api-docs/**",
                           "/swagger-ui/**",
                           "/swagger-ui.html"
                   ).permitAll()
                   .anyRequest()
                   .authenticated())
   .addFilterBefore(filter,
           UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}
