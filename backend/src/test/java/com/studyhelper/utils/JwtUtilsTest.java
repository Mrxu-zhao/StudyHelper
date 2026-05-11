package com.studyhelper.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "StudyHelperSecretKey2026ForJwtTokenGenerationMustBeAtLeast256Bits");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 86400000L);
        ReflectionTestUtils.setField(jwtUtils, "header", "Authorization");
        ReflectionTestUtils.setField(jwtUtils, "prefix", "Bearer");
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testParseToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertEquals(1L, jwtUtils.getUserIdFromToken(token));
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
        assertEquals("student", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testValidateToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertTrue(jwtUtils.validateToken(token));
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }
}
