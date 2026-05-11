package com.studyhelper.dto;

import lombok.Data;

@Data
public class PlanVO {
    private Long planId;
    private String name;
    private Integer type;
    private String targetDate;
    private Integer totalDays;
    private Integer completedDays;
    private Integer status;
    private String statusName;
    private String description;
    private String createdAt;
}
