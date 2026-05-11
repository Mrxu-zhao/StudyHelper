package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("papers")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    @TableField("subject_id")
    private Long subjectId;
    
    @TableField("grade_level")
    private Integer gradeLevel;
    
    @TableField("total_score")
    private Integer totalScore;
    
    @TableField("total_time")
    private Integer totalTime;
    
    @TableField("question_count")
    private Integer questionCount;
    
    @TableField("difficulty_avg")
    private BigDecimal difficultyAvg;
    
    @TableField("source_type")
    private Integer sourceType;
    
    @TableField("created_by")
    private Long createdBy;
    
    private String description;
    
    @TableField("is_published")
    private Integer isPublished;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
