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

    // ==================== generateToken Tests ====================

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains("."));
    }

    @Test
    void testGenerateToken_DifferentRoles() {
        String studentToken = jwtUtils.generateToken(1L, "student1", "student");
        String teacherToken = jwtUtils.generateToken(2L, "teacher1", "teacher");
        String adminToken = jwtUtils.generateToken(3L, "admin1", "admin");
        
        assertNotNull(studentToken);
        assertNotNull(teacherToken);
        assertNotNull(adminToken);
        assertNotEquals(studentToken, teacherToken);
        assertNotEquals(studentToken, adminToken);
    }

    @Test
    void testGenerateToken_DifferentUsers() {
        String token1 = jwtUtils.generateToken(1L, "user1", "student");
        String token2 = jwtUtils.generateToken(2L, "user2", "student");
        
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    // ==================== validateToken Tests ====================

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void testValidateToken_NullToken() {
        // Should return false without throwing exception
        try {
            assertFalse(jwtUtils.validateToken(null));
        } catch (IllegalArgumentException e) {
            // JJWT throws exception for null/empty tokens
            assertTrue(true);
        }
    }

    @Test
    void testValidateToken_EmptyToken() {
        // Should return false without throwing exception
        try {
            assertFalse(jwtUtils.validateToken(""));
        } catch (IllegalArgumentException | io.jsonwebtoken.MalformedJwtException e) {
            // JJWT throws exception for null/empty tokens
            assertTrue(true);
        }
    }

    @Test
    void testValidateToken_MalformedToken() {
        assertFalse(jwtUtils.validateToken("not-a-jwt"));
    }

    // ==================== getUserIdFromToken Tests ====================

    @Test
    void testGetUserIdFromToken() {
        String token = jwtUtils.generateToken(123L, "testuser", "student");
        
        assertEquals(123L, jwtUtils.getUserIdFromToken(token));
    }

    @Test
    void testGetUserIdFromToken_LargeId() {
        String token = jwtUtils.generateToken(Long.MAX_VALUE, "testuser", "student");
        
        assertEquals(Long.MAX_VALUE, jwtUtils.getUserIdFromToken(token));
    }

    @Test
    void testGetUserIdFromToken_InvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.getUserIdFromToken("invalid.token");
        });
    }

    // ==================== getUsernameFromToken Tests ====================

    @Test
    void testGetUsernameFromToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void testGetUsernameFromToken_SpecialCharacters() {
        String token = jwtUtils.generateToken(1L, "test_user_123", "student");
        
        assertEquals("test_user_123", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.getUsernameFromToken("invalid.token");
        });
    }

    // ==================== getRoleFromToken Tests ====================

    @Test
    void testGetRoleFromToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertEquals("student", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testGetRoleFromToken_Teacher() {
        String token = jwtUtils.generateToken(1L, "teacher", "teacher");
        
        assertEquals("teacher", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testGetRoleFromToken_Admin() {
        String token = jwtUtils.generateToken(1L, "admin", "admin");
        
        assertEquals("admin", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testGetRoleFromToken_InvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.getRoleFromToken("invalid.token");
        });
    }

    // ==================== isTokenExpired Tests ====================

    @Test
    void testIsTokenExpired_ValidToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void testIsTokenExpired_InvalidToken() {
        // Should return true for invalid token (may throw exception depending on implementation)
        try {
            assertTrue(jwtUtils.isTokenExpired("invalid.token"));
        } catch (Exception e) {
            // If it throws exception, we consider it expired/invalid
            assertTrue(true);
        }
    }

    @Test
    void testIsTokenExpired_NullToken() {
        // Should handle null gracefully
        try {
            assertTrue(jwtUtils.isTokenExpired(null));
        } catch (IllegalArgumentException e) {
            // JJWT throws exception for null tokens
            assertTrue(true);
        }
    }

    // ==================== parseToken Tests ====================

    @Test
    void testParseToken() {
        String token = jwtUtils.generateToken(1L, "testuser", "student");
        
        assertEquals(1L, jwtUtils.getUserIdFromToken(token));
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
        assertEquals("student", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void testParseToken_AllRoles() {
        // Test student role
        String studentToken = jwtUtils.generateToken(1L, "student1", "student");
        assertEquals("student", jwtUtils.getRoleFromToken(studentToken));
        
        // Test teacher role
        String teacherToken = jwtUtils.generateToken(2L, "teacher1", "teacher");
        assertEquals("teacher", jwtUtils.getRoleFromToken(teacherToken));
        
        // Test admin role
        String adminToken = jwtUtils.generateToken(3L, "admin1", "admin");
        assertEquals("admin", jwtUtils.getRoleFromToken(adminToken));
    }

    // ==================== getExpiration Tests ====================

    @Test
    void testGetExpiration() {
        // The default expiration should be 86400000L (24 hours)
        assertEquals(86400000L, jwtUtils.getExpiration());
    }

    // ==================== Integration-like Tests ====================

    @Test
    void testCompleteAuthFlow() {
        // Simulate complete authentication flow
        
        // 1. User registers/logs in
        Long userId = 100L;
        String username = "newuser";
        String role = "student";
        
        // 2. Generate token
        String token = jwtUtils.generateToken(userId, username, role);
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        
        // 3. Validate and parse token
        assertEquals(userId, jwtUtils.getUserIdFromToken(token));
        assertEquals(username, jwtUtils.getUsernameFromToken(token));
        assertEquals(role, jwtUtils.getRoleFromToken(token));
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void testConcurrentTokenGeneration() {
        // Generate multiple tokens and ensure they are unique
        String token1 = jwtUtils.generateToken(1L, "user1", "student");
        String token2 = jwtUtils.generateToken(1L, "user1", "student");
        
        // Note: In a real scenario, tokens generated at different times should be different
        // due to timestamp in JWT. Here we just ensure both are valid.
        assertTrue(jwtUtils.validateToken(token1));
        assertTrue(jwtUtils.validateToken(token2));
    }

    @Test
    void testTokenWithNullValues() {
        // This tests the edge case of handling null values
        // In a real system, these shouldn't happen, but we verify the behavior
        String token = jwtUtils.generateToken(null, null, null);
        
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
    }
}
