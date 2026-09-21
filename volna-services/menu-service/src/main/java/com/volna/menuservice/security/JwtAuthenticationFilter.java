package com.volna.menuservice.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    public JwtAuthenticationFilter(JwtService jwt){this.jwt=jwt;}
    protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
        String h=req.getHeader("Authorization");
        if(h!=null && h.startsWith("Bearer ")){
            try{
                var c=jwt.claims(h.substring(7));
                List<SimpleGrantedAuthority> authorities=new ArrayList<>();
                Object roles=c.get("roles");
                if(roles instanceof Collection<?> collection){
                    for(Object role:collection){
                        if(role!=null) authorities.add(new SimpleGrantedAuthority("ROLE_"+role.toString()));
                    }
                }
                if(authorities.isEmpty()) authorities.add(new SimpleGrantedAuthority("ROLE_AUTHENTICATED"));
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(c.getSubject(),null,authorities));
            }catch(Exception ignored){}
        }
        chain.doFilter(req,res);
    }
}
