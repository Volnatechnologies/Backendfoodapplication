/*package com.volna.authservice.security;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable())
          .authorizeHttpRequests(a
                  ->a.requestMatchers("/api/v1/auth/**",
                  "/actuator/health","/actuator/info").permitAll()
                  .anyRequest().authenticated());
  return http.build();
 }
}*/
package com.volna.authservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

 @Bean
 PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
 }

 @Bean
 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

  http
          .csrf(c -> c.disable())

          .cors(cors -> cors.configurationSource(corsConfigurationSource()))

          .sessionManagement(session ->
                  session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )

          .authorizeHttpRequests(a -> a
                  .requestMatchers(
                          "/api/v1/auth/**",
                          "/actuator/health",
                          "/actuator/info"
                  ).permitAll()
                  .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                  .anyRequest().authenticated()
          );

  return http.build();
 }

 @Bean
 CorsConfigurationSource corsConfigurationSource() {

  CorsConfiguration configuration = new CorsConfiguration();

  configuration.setAllowedOrigins(
          List.of("http://localhost:5173")
  );

  configuration.setAllowedMethods(
          List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
  );

  configuration.setAllowedHeaders(
          List.of("*")
  );

  configuration.setAllowCredentials(true);

  UrlBasedCorsConfigurationSource source =
          new UrlBasedCorsConfigurationSource();

  source.registerCorsConfiguration("/**", configuration);

  return source;
 }
}