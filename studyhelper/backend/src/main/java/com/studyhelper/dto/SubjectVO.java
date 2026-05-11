package com.studyhelper.dto;

import lombok.Data;

@Data
public class SubjectVO {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private String icon;
    private Integer gradeLevel;
    private Integer chapterCount;
    private Integer knowledgeCount;
}
