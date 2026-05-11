package com.studyhelper.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ExamResultVO {
    private Long recordId;
    private Integer totalScore;
    private Integer obtainedScore;
    private BigDecimal scoreRate;
    private Integer correctCount;
    private Integer wrongCount;
    private List<Long> wrongQuestionIds;
}
