package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_questions")
public class WrongQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("question_id")
    private Long questionId;
    
    @TableField("paper_id")
    private Long paperId;
    
    @TableField("exam_record_id")
    private Long examRecordId;
    
    @TableField("user_answer")
    private String userAnswer;
    
    @TableField("correct_answer")
    private String correctAnswer;
    
    @TableField("is_reviewed")
    private Integer isReviewed;
    
    @TableField("review_count")
    private Integer reviewCount;
    
    @TableField("last_review_at")
    private LocalDateTime lastReviewAt;
    
    @TableField("review_note")
    private String reviewNote;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
