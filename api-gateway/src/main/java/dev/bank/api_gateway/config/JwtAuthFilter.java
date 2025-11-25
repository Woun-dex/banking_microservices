package dev.bank.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String SecretKey ;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
    
        // Allow CORS preflight requests (OPTIONS) to pass through
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String requestPath = request.getRequestURI();
        // Skip auth for public endpoints
        if (requestPath.equals("/api/users/auth/login") ||
            requestPath.equals("/api/users/auth/token") ||
            requestPath.equals("/user/login") ||
            requestPath.equals("/user/token") ||
            (requestPath.equals("/api/users/user") && request.getMethod().equals("POST")) ||
            (requestPath.equals("/user") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }
                 
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                var claims = Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(SecretKey.getBytes()))
                            .build()
                            .parseSignedClaims(jwt);
                
                String userId = claims.getPayload().getSubject();
                String role = (String) claims.getPayload().get("role");
                
                // Set request attributes for downstream use
                request.setAttribute("userId", userId);
                request.setAttribute("role", role);
                
                // Create Spring Security Authentication token
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + (role != null ? role.toUpperCase() : "USER"))
                );
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }
    
}
