package com.studyhelper.controller;

import com.studyhelper.common.Result;
import com.studyhelper.dto.*;
import com.studyhelper.entity.User;
import com.studyhelper.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        LoginVO vo = userService.register(dto);
        return Result.success("注册成功", vo);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success("登录成功", vo);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserProfileVO vo = userService.getProfile(userId);
        return Result.success(vo);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success("更新成功", null);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, dto);
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 家长绑定学生
     */
    @PostMapping("/bind")
    public Result<Void> bindStudent(HttpServletRequest request, @RequestBody java.util.Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        String bindCode = params.get("bindCode");
        userService.bindStudent(userId, bindCode);
        return Result.success("绑定成功", null);
    }
    
    /**
     * 获取绑定学生列表
     */
    @GetMapping("/bound-students")
    public Result<List<User>> getBoundStudents(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<User> students = userService.getBoundStudents(userId);
        return Result.success(students);
    }
}
