package com.questionbank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PracticeSubmitDTO {
    private Long subjectId;
    @NotNull(message = "答题记录不能为空")
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;
        private String userAnswer;
        private Integer sortOrder;
    }
}
