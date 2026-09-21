/*package com.volna.restaurantservice.security;
import lombok.RequiredArgsConstructor; import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.web.*; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
 private final JwtAuthenticationFilter filter;
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable())
          .sessionManagement(s->
                  s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->
           a.requestMatchers("/actuator/health","/actuator/info")
                   .permitAll()
                   .requestMatchers("/api/v1/restaurants/**").authenticated()
                   .anyRequest()
                   .authenticated())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}*/
package com.volna.restaurantservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

 private final JwtAuthenticationFilter filter;

 @Bean
 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

  http
          .csrf(c -> c.disable())

          .cors(cors ->
                  cors.configurationSource(corsConfigurationSource())
          )

          .sessionManagement(s ->
                  s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )

          .authorizeHttpRequests(a ->
                  a
                          .requestMatchers(
                                  "/actuator/health",
                                  "/actuator/info"
                          ).permitAll()

                          .requestMatchers(
                                  "/api/v1/restaurants/**"
                          ).authenticated()

                          .requestMatchers(
                                  org.springframework.http.HttpMethod.OPTIONS,
                                  "/**"
                          ).permitAll()

                          .anyRequest()
                          .authenticated()
          )

          .addFilterBefore(
                  filter,
                  UsernamePasswordAuthenticationFilter.class
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
          List.of(
                  "GET",
                  "POST",
                  "PUT",
                  "DELETE",
                  "PATCH",
                  "OPTIONS"
          )
  );

  configuration.setAllowedHeaders(
          List.of("*")
  );

  configuration.setAllowCredentials(true);

  UrlBasedCorsConfigurationSource source =
          new UrlBasedCorsConfigurationSource();

  source.registerCorsConfiguration(
          "/**",
          configuration
  );

  return source;
 }
}


