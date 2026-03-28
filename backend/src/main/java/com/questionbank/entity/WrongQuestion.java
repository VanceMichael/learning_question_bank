package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wrong_question")
public class WrongQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long questionId;

    private Long subjectId;

    private Integer wrongCount;

    private LocalDateTime firstWrongAt;

    private LocalDateTime lastWrongAt;

    @TableField(exist = false)
    private Question question;

    @TableField(exist = false)
    private String subjectName;
}