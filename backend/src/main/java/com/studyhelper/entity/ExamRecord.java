package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("exam_records")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("paper_id")
    private Long paperId;
    
    @TableField("total_score")
    private Integer totalScore;
    
    @TableField("obtained_score")
    private Integer obtainedScore;
    
    @TableField("score_rate")
    private BigDecimal scoreRate;
    
    private Integer duration;
    
    @TableField("answers_json")
    private String answersJson;
    
    @TableField("is_completed")
    private Integer isCompleted;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
