package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhelper.dto.*;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.GradeMapper;
import com.studyhelper.mapper.UserMapper;
import com.studyhelper.utils.JwtUtils;
import com.studyhelper.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private GradeMapper gradeMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtils.encode("password123"));
        testUser.setRole("student");
        testUser.setRealName("Test User");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setGrade(1);
        testUser.setStatus(1);
        testUser.setBindCode("ABC123");
        testUser.setAvatarUrl("http://example.com/avatar.jpg");
        testUser.setCreatedAt(LocalDateTime.now());
        
        // Default mock setup for JWT
        when(jwtUtils.generateToken(any(), any(), any())).thenReturn("test-token");
        when(jwtUtils.getExpiration()).thenReturn(86400000L);
    }

    // ==================== Register Tests ====================

    @Test
    void register_Success() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setRole("student");
        dto.setRealName("New User");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        LoginVO result = userService.register(dto);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("student", result.getRole());
        assertEquals("test-token", result.getToken());
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("existinguser");
        dto.setPassword("password123");
        dto.setRole("student");
        dto.setRealName("Test User");

        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(dto);
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    // ==================== Login Tests ====================

    @Test
    void login_UserNotFound_ThrowsException() {
        // Given
        LoginDTO dto = new LoginDTO();
        dto.setUsername("nonexistent");
        dto.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(dto);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Given
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(dto);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void login_DisabledUser_ThrowsException() {
        // Given
        testUser.setStatus(0);
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(dto);
        });
        assertEquals("账号已被禁用", exception.getMessage());
    }

    // ==================== getProfile Tests ====================

    @Test
    void getProfile_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        UserProfileVO result = userService.getProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getRealName());
        assertEquals("138****8000", result.getPhone());
        assertEquals("t***@example.com", result.getEmail());
        assertEquals("student", result.getRole());
    }

    @Test
    void getProfile_UserNotFound_ThrowsException() {
        // Given
        when(userMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getProfile(999L);
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void getProfile_NullPhoneAndEmail() {
        // Given
        testUser.setPhone(null);
        testUser.setEmail(null);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        UserProfileVO result = userService.getProfile(1L);

        // Then
        assertNotNull(result);
        assertNull(result.getPhone());
        assertNull(result.getEmail());
    }

    @Test
    void getProfile_WithoutGrade() {
        // Given
        testUser.setGrade(null);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        UserProfileVO result = userService.getProfile(1L);

        // Then
        assertNotNull(result);
        assertNull(result.getGradeName());
    }

    // ==================== Change Password Tests ====================

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        // Given
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("password123");
        dto.setNewPassword("newpassword456");

        when(userMapper.selectById(999L)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(999L, dto);
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void changePassword_WrongOldPassword_ThrowsException() {
        // Given
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setOldPassword("wrongpassword");
        dto.setNewPassword("newpassword456");

        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(1L, dto);
        });
        assertEquals("原密码错误", exception.getMessage());
    }

    // ==================== Bind Student Tests ====================

    @Test
    void bindStudent_InvalidBindCode_ThrowsException() {
        // Given
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.bindStudent(1L, "INVALID");
        });
        assertEquals("绑定码无效", exception.getMessage());
    }
}
