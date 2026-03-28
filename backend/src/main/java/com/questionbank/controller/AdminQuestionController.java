package com.questionbank.controller;

import com.questionbank.common.PageResult;
import com.questionbank.common.Result;
import com.questionbank.dto.QuestionDTO;
import com.questionbank.entity.Question;
import com.questionbank.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Result<PageResult<Question>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.ok(questionService.page(current, size, subjectId, type, keyword, status));
    }

    @PostMapping
    public Result<Question> create(@Valid @RequestBody QuestionDTO dto) {
        return Result.ok(questionService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Question> update(@PathVariable Long id, @Valid @RequestBody QuestionDTO dto) {
        return Result.ok(questionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Result.ok();
    }
}
