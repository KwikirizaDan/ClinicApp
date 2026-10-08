package com.clinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT Utility Tests - Tests token generation and validation.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForClinicAppThatIsAtLeast256BitsLongForHS256");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("Should generate a valid JWT token")
    void testGenerateToken() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        assertNotNull(token);
        assertTrue(token.length() > 0);
        // JWT has 3 parts separated by dots
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsername() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        String username = jwtUtil.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("Should extract role from token")
    void testExtractRole() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        String role = jwtUtil.extractRole(token);

        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    @DisplayName("Should validate a correct token")
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Should reject an invalid token")
    void testValidateToken_Invalid() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    @DisplayName("Should reject a tampered token")
    void testValidateToken_Tampered() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertFalse(jwtUtil.validateToken(tampered));
    }
}
