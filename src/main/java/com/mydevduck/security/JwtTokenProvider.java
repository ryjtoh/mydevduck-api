package com.mydevduck.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration; // 1 hour

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration; // 7 days

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable must be set");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        log.info("JWT secret validation passed");
    }

    /**
     * Generate access token with userId, email, and role claims
     * Expires in 1 hour
     */
    public String generateAccessToken(UUID userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("email", email);
        claims.put("role", role);

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSignKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Generate refresh token with only userId claim
     * Expires in 7 days
     */
    public String generateRefreshToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSignKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Validate token - checks signature and expiration
     * Returns true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract userId from token claims
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    /**
     * Extract email from token (subject)
     */
    public String getEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract role from token claims
     */
    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract expiration date from token
     */
    public Date getExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract issued at date from token
     */
    public Date getIssuedAtFromToken(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get signing key using HS512 algorithm
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
