package com.studyhelper.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String realName;
    private String phone;
    private String email;
    private Integer grade;
    private String avatar;
    private String studyTarget;
    private String[] weakSubjects;
}
