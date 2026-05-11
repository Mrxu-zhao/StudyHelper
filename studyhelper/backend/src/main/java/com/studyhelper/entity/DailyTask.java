package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_tasks")
public class DailyTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("plan_id")
    private Long planId;
    
    @TableField("task_date")
    private LocalDate taskDate;
    
    @TableField("subject_id")
    private Long subjectId;
    
    @TableField("chapter_id")
    private Long chapterId;
    
    @TableField("knowledge_ids")
    private String knowledgeIds;
    
    @TableField("target_count")
    private Integer targetCount;
    
    @TableField("completed_count")
    private Integer completedCount;
    
    private Integer status;
    
    @TableField("estimated_time")
    private Integer estimatedTime;
    
    @TableField("actual_time")
    private Integer actualTime;
    
    private String notes;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
