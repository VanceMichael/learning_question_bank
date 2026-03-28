package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("practice_detail")
public class PracticeDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Integer sortOrder;

    @TableField(exist = false)
    private Question question;
}
