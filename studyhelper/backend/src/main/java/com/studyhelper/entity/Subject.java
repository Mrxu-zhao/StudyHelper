package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("subjects")
public class Subject {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String code;
    
    @TableField("grade_level")
    private Integer gradeLevel;
    
    @TableField("icon_url")
    private String iconUrl;
    
    private String description;
    
    @TableField("is_active")
    private Integer isActive;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
