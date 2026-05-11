package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorites")
public class UserFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("favorite_type")
    private Integer favoriteType;
    
    @TableField("target_id")
    private Long targetId;
    
    private String note;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
