package com.volna.inventory_service.security;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtTokenProvider provider;
 public JwtAuthenticationFilter(JwtTokenProvider provider){this.provider=provider;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  if(h==null||!h.startsWith("Bearer ")){chain.doFilter(req,res);return;}
  String token=h.substring(7);
  if(provider.valid(token)) try{ Claims c=provider.claims(token); UUID id=provider.userId(token); List<String> roles=c.get("roles",List.class); var auth=roles==null?Collections.<SimpleGrantedAuthority>emptyList():roles.stream().map(r->new SimpleGrantedAuthority("ROLE_"+r)).toList(); String role=roles!=null&&!roles.isEmpty()?roles.get(0):"USER"; var principal=new AuthenticatedUser(id,role); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,null,auth)); }catch(Exception e){SecurityContextHolder.clearContext();}
  chain.doFilter(req,res);
 }
}
