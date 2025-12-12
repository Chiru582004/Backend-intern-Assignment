package com.example.multitenant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public JwtFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Jws<Claims> claims = jwtUtil.validate(token);
                String adminId = claims.getBody().getSubject();
                String orgId = claims.getBody().get("orgId", String.class);
                // simple authentication principal with orgId in details
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(adminId, null, Collections.emptyList());
                auth.setDetails(orgId); // we will retrieve orgId from auth.getDetails()
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                // invalid token -> leave security context empty
            }
        }
        chain.doFilter(req, res);
    }
}
