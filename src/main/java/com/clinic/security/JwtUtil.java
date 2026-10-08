package com.clinic.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility Class - Handles token generation and validation.
 *
 * JWT (JSON Web Token) Structure:
 * ┌─────────────────────────────────────────────────────────────┐
 * │  HEADER          │  PAYLOAD (Claims)         │  SIGNATURE   │
 * │  {alg: HS256}    │  {sub: username,          │  HMAC-SHA256 │
 * │                  │   roles: [ROLE_ADMIN],    │  (signed     │
 * │                  │   exp: expiration}        │  with secret)│
 * └─────────────────────────────────────────────────────────────┘
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForClinicAppThatIsAtLeast256BitsLongForHS256}")
    private String secret;

    @Value("${jwt.expiration:86400000}")  // 24 hours in milliseconds
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a JWT token for a user.
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from token.
     */
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * Extract role from token.
     */
    public String extractRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * Validate token - checks signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
