package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("grades")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Integer level;
    
    private String description;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
