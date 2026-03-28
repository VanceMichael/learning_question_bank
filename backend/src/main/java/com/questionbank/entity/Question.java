package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "question", autoResultMap = true)
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long subjectId;
    private Long creatorId;
    private Integer type;
    private String content;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object answer;
    private String analysis;
    private Integer difficulty;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String subjectName;
    @TableField(exist = false)
    private String creatorName;
}
