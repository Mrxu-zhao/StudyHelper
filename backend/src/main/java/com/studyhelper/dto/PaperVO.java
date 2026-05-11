package com.studyhelper.dto;

import lombok.Data;

@Data
public class PaperVO {
    private Long paperId;
    private String name;
    private Long subjectId;
    private String subjectName;
    private Integer gradeLevel;
    private Integer totalScore;
    private Integer totalTime;
    private Integer questionCount;
    private Integer difficultyAvg;
    private Integer sourceType;
    private String description;
    private Integer isPublished;
    private String createdAt;
}
