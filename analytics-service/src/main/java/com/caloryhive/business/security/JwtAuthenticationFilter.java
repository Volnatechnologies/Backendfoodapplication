package com.caloryhive.business.security;

import com.caloryhive.business.business.entity.Business;
import com.caloryhive.business.business.repository.BusinessRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final BusinessRepository businessRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            if (jwtService.validateToken(jwt)) {
                UUID userId = jwtService.extractUserId(jwt);
                UUID businessId = jwtService.extractBusinessId(jwt);
                String email = jwtService.extractEmail(jwt);
                List<String> roles = jwtService.extractRoles(jwt);

                // If businessId is not directly in the JWT, lookup business by owner or user
                if (businessId == null && userId != null) {
                    businessId = businessRepository.findByOwnerId(userId)
                            .map(Business::getId)
                            .orElse(UUID.fromString("00000000-0000-0000-0000-000000000001"));
                }

                if (businessId == null) {
                    businessId = UUID.fromString("00000000-0000-0000-0000-000000000001");
                }

                TenantContext.setBusinessId(businessId);

                var authorities = roles.stream()
                        .map(r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                if (authorities.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_BUSINESS_OWNER"));
                }

                UserPrincipal principal = UserPrincipal.builder()
                        .id(userId)
                        .businessId(businessId)
                        .email(email)
                        .authorities(authorities)
                        .enabled(true)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("Could not set user authentication in security context: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            TenantContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
