package com.studyhelper.dto;

import lombok.Data;

@Data
public class LoginVO {
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private Integer grade;
    private String avatar;
    private String token;
    private String bindCode;
    private Long expiresIn;
}
