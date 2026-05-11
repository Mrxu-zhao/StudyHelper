package com.studyhelper.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDTO {
    private LocalDate taskDate;
    private Long subjectId;
    private Long chapterId;
    private List<Long> knowledgeIds;
    private Integer targetCount;
    private Integer estimatedTime;
    private String notes;
}
