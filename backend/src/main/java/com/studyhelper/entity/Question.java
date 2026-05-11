package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("questions")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("subject_id")
    private Long subjectId;
    
    @TableField("chapter_id")
    private Long chapterId;
    
    private Integer type;
    
    private Integer difficulty;
    
    private Integer score;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private String analysis;
    
    @TableField("knowledge_ids")
    private String knowledgeIds;
    
    @TableField("usage_count")
    private Integer usageCount;
    
    @TableField("correct_count")
    private Integer correctCount;
    
    @TableField("correct_rate")
    private java.math.BigDecimal correctRate;
    
    private String source;
    
    @TableField("is_active")
    private Integer isActive;
    
    @TableField("created_by")
    private Long createdBy;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
