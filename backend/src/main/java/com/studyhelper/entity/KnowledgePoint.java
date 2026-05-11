package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_points")
public class KnowledgePoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("chapter_id")
    private Long chapterId;
    
    private String title;
    
    private String content;
    
    private Integer importance;
    
    private Integer difficulty;
    
    private String frequency;
    
    private String tags;
    
    @TableField("related_ids")
    private String relatedIds;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField("favorite_count")
    private Integer favoriteCount;
    
    @TableField("is_active")
    private Integer isActive;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
