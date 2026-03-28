package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "wrong_question", autoResultMap = true)
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
    
    // 临时字段用于MyBatis映射
    @TableField(exist = false)
    private Long qId;
    
    @TableField(exist = false)
    private Long qSubjectId;
    
    @TableField(exist = false)
    private Long qCreatorId;
    
    @TableField(exist = false)
    private Integer qType;
    
    @TableField(exist = false)
    private String qContent;
    
    @TableField(exist = false)
    private String qOptions;
    
    @TableField(exist = false)
    private Object qAnswer;
    
    @TableField(exist = false)
    private String qAnalysis;
    
    @TableField(exist = false)
    private Integer qDifficulty;
    
    @TableField(exist = false)
    private Integer qStatus;
    
    @TableField(exist = false)
    private LocalDateTime qCreatedAt;
    
    @TableField(exist = false)
    private LocalDateTime qUpdatedAt;
}