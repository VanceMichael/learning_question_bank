package com.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    @NotNull(message = "科目不能为空")
    private Long subjectId;
    @NotNull(message = "题型不能为空")
    private Integer type;
    @NotBlank(message = "题目内容不能为空")
    private String content;
    private List<String> options;
    @NotNull(message = "答案不能为空")
    private Object answer;
    private String analysis;
    private Integer difficulty;
    private Integer status;
}
