package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("chapters")
public class Chapter {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("subject_id")
    private Long subjectId;
    
    @TableField("parent_id")
    private Long parentId;
    
    private String name;
    
    private String code;
    
    @TableField("order_num")
    private Integer orderNum;
    
    private Integer level;
    
    private String description;
    
    @TableField("knowledge_count")
    private Integer knowledgeCount;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<Chapter> children;
}
