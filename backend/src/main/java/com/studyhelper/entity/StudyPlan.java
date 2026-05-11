package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("study_plans")
public class StudyPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private String name;
    
    private Integer type;
    
    @TableField("target_date")
    private LocalDate targetDate;
    
    @TableField("total_days")
    private Integer totalDays;
    
    @TableField("completed_days")
    private Integer completedDays;
    
    private Integer status;
    
    private String description;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
