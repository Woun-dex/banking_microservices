package dev.bank.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String SecretKey ;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
    
        String requestPath = request.getRequestURI();
        if (requestPath.equals("/api/users/auth/token") || 
            requestPath.equals("/user/token") ||
            requestPath.startsWith("/api/users/user") && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }
                 
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                var claims= Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(SecretKey.getBytes()))
                            .build()
                            .parseSignedClaims(jwt);
                    

                
                request.setAttribute("userId", claims.getPayload().getSubject());
                request.setAttribute("role", claims.getPayload().get("role"));
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
