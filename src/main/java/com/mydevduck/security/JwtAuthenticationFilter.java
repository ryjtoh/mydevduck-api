package com.mydevduck.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract token from Authorization header
            String token = extractTokenFromRequest(request);

            // If token exists and is valid, authenticate the user
            if (token != null && jwtTokenProvider.validateToken(token)) {
                authenticateUser(token, request);
            }

            // Continue with filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            handleAuthenticationError(response, "Invalid or expired token");
            // DO NOT continue filter chain - error response already sent
        }
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }

    /**
     * Authenticate user and set SecurityContext
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        try {
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            String email = jwtTokenProvider.getEmailFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            // Validate required claims are present
            if (userId == null || email == null || role == null) {
                log.warn("Token missing required claims - userId: {}, email: {}, role: {}",
                        userId, email, role);
                throw new RuntimeException("Token missing required claims");
            }

            // Create authority from role
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // Create authentication token with userId as principal
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId, // principal is userId
                            null,   // credentials (not needed for JWT)
                            Collections.singletonList(authority)
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user: {} ({}), role: {}", email, userId, role);

        } catch (Exception e) {
            log.error("Failed to authenticate user from token: {}", e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
    }

    /**
     * Handle authentication errors by sending JSON error response
     */
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("timestamp", System.currentTimeMillis());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
