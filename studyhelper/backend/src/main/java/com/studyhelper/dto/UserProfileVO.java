package com.studyhelper.dto;

import lombok.Data;

@Data
public class UserProfileVO {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String role;
    private Integer grade;
    private String gradeName;
    private String avatar;
    private String bindCode;
    private String studyTarget;
    private String[] weakSubjects;
    private String createdAt;
}
