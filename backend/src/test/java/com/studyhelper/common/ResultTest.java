package com.studyhelper.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccess() {
        Result<String> result = Result.success("test data");
        
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("test data", result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithMessage() {
        Result<String> result = Result.success("Operation successful", "data");
        
        assertEquals(200, result.getCode());
        assertEquals("Operation successful", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    void testError() {
        Result<Void> result = Result.error("Error message");
        
        assertEquals(500, result.getCode());
        assertEquals("Error message", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testBuild() {
        Result<String> result = Result.build(400, "Bad request", "error data");
        
        assertEquals(400, result.getCode());
        assertEquals("Bad request", result.getMessage());
        assertEquals("error data", result.getData());
    }
}
