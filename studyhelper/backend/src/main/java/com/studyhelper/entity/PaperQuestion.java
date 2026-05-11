package com.studyhelper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("paper_questions")
public class PaperQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("paper_id")
    private Long paperId;
    
    @TableField("question_id")
    private Long questionId;
    
    @TableField("order_num")
    private Integer orderNum;
    
    private Integer score;
    
    @TableField("is_required")
    private Integer isRequired;
}
