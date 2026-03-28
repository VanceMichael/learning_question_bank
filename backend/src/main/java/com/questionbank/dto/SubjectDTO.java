package com.questionbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectDTO {
    private Long id;
    @NotBlank(message = "科目名称不能为空")
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
