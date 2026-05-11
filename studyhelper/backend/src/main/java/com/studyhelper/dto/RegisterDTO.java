package com.studyhelper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 6, max = 20, message = "用户名长度需在6-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户名只能包含字母和数字")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度需在8-20位之间")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    private String role;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String phone;
    
    private String email;
    
    private Integer grade;
}
