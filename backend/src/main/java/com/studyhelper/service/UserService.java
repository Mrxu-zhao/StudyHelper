package com.studyhelper.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.studyhelper.common.Constants;
import com.studyhelper.dto.*;
import com.studyhelper.entity.Grade;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.GradeMapper;
import com.studyhelper.mapper.UserMapper;
import com.studyhelper.utils.JwtUtils;
import com.studyhelper.utils.PasswordUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final GradeMapper gradeMapper;
    private final JwtUtils jwtUtils;
    
    public UserService(UserMapper userMapper, GradeMapper gradeMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.gradeMapper = gradeMapper;
        this.jwtUtils = jwtUtils;
    }
    
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtils.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGrade(dto.getGrade());
        user.setStatus(1);
        user.setBindCode(PasswordUtils.generateBindCode());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setBindCode(user.getBindCode());
        vo.setToken(token);
        
        return vo;
    }
    
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!PasswordUtils.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 更新最后登录时间
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId())
                .set(User::getLastLoginAt, LocalDateTime.now());
        userMapper.update(null, updateWrapper);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 返回登录信息
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setGrade(user.getGrade());
        vo.setAvatar(user.getAvatarUrl());
        vo.setToken(token);
        vo.setExpiresIn(jwtUtils.getExpiration());
        
        return vo;
    }
    
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setEmail(maskEmail(user.getEmail()));
        vo.setRole(user.getRole());
        vo.setGrade(user.getGrade());
        vo.setGradeName(getGradeName(user.getGrade()));
        vo.setAvatar(user.getAvatarUrl());
        vo.setBindCode(user.getBindCode());
        vo.setStudyTarget(user.getStudyTarget());
        vo.setWeakSubjects(parseWeakSubjects(user.getWeakSubjects()));
        vo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        
        return vo;
    }
    
    @Transactional
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        
        if (dto.getRealName() != null) {
            wrapper.set(User::getRealName, dto.getRealName());
        }
        if (dto.getPhone() != null) {
            wrapper.set(User::getPhone, dto.getPhone());
        }
        if (dto.getEmail() != null) {
            wrapper.set(User::getEmail, dto.getEmail());
        }
        if (dto.getGrade() != null) {
            wrapper.set(User::getGrade, dto.getGrade());
        }
        if (dto.getAvatar() != null) {
            wrapper.set(User::getAvatarUrl, dto.getAvatar());
        }
        if (dto.getStudyTarget() != null) {
            wrapper.set(User::getStudyTarget, dto.getStudyTarget());
        }
        if (dto.getWeakSubjects() != null) {
            wrapper.set(User::getWeakSubjects, String.join(",", dto.getWeakSubjects()));
        }
        
        userMapper.update(null, wrapper);
    }
    
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码
        if (!PasswordUtils.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
                .set(User::getPassword, PasswordUtils.encode(dto.getNewPassword()));
        userMapper.update(null, wrapper);
    }
    
    @Transactional
    public void bindStudent(Long parentId, String bindCode) {
        // 查找学生
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getBindCode, bindCode)
                .eq(User::getRole, Constants.ROLE_STUDENT);
        User student = userMapper.selectOne(wrapper);
        
        if (student == null) {
            throw new RuntimeException("绑定码无效");
        }
        
        // 绑定家长
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, student.getId())
                .set(User::getParentUserId, parentId);
        userMapper.update(null, updateWrapper);
    }
    
    public List<User> getBoundStudents(Long parentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getParentUserId, parentId)
                .eq(User::getRole, Constants.ROLE_STUDENT);
        return userMapper.selectList(wrapper);
    }
    
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return parts[0] + "***@" + parts[1];
        }
        return parts[0].charAt(0) + "***@" + parts[1];
    }
    
    private String getGradeName(Integer grade) {
        if (grade == null) return null;
        return switch (grade) {
            case 1 -> "高一";
            case 2 -> "高二";
            case 3 -> "高三";
            default -> null;
        };
    }
    
    private String[] parseWeakSubjects(String weakSubjects) {
        if (weakSubjects == null || weakSubjects.isEmpty()) {
            return null;
        }
        return weakSubjects.split(",");
    }
}
