package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String role;
    
    @TableField("real_name")
    private String realName;
    
    private String phone;
    
    private String email;
    
    private Integer grade;
    
    @TableField("avatar_url")
    private String avatarUrl;
    
    @TableField("bind_code")
    private String bindCode;
    
    @TableField("parent_user_id")
    private Long parentUserId;
    
    @TableField("study_target")
    private String studyTarget;
    
    @TableField("weak_subjects")
    private String weakSubjects;
    
    @TableField("admin_level")
    private Integer adminLevel;
    
    private Integer status;
    
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
