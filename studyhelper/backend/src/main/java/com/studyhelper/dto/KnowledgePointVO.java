package com.studyhelper.dto;

import lombok.Data;

@Data
public class KnowledgePointVO {
    private Long knowledgeId;
    private Long chapterId;
    private String title;
    private Integer importanceLevel;
    private Integer difficultyLevel;
    private String examFrequency;
    private String content;
    private String tags;
    private Integer viewCount;
}
