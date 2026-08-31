package com.volna.restaurantservice.security;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.*;
@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtTokenProvider provider;
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)
         throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  if(h==null||!h.startsWith("Bearer ")){
   chain.doFilter(req,res);
   return;
  }
  try{
   Claims c=provider.parse(h.substring(7));
   String userId=c.getSubject();
   List<String> roles=c.get("roles",List.class);
   var authorities=roles==null?List.
           <SimpleGrantedAuthority>of():roles
           .stream().map(x->new SimpleGrantedAuthority("ROLE_"+x)).toList();
   SecurityContextHolder.getContext()
           .setAuthentication(new UsernamePasswordAuthenticationToken(
                   userId,null,authorities));
  }
  catch(Exception e){
   SecurityContextHolder.clearContext();
  }
  chain.doFilter(req,res);
 }
}