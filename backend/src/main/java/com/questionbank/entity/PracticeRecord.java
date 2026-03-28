package com.questionbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("practice_record")
public class PracticeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long subjectId;
    private Integer totalCount;
    private Integer correctCount;
    private BigDecimal accuracy;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @TableField(exist = false)
    private String subjectName;
}
