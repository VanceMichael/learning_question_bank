package com.questionbank.controller;

import com.questionbank.common.Result;
import com.questionbank.entity.Question;
import com.questionbank.entity.WrongQuestion;
import com.questionbank.service.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wrong-questions")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    @GetMapping
    public Result<List<WrongQuestion>> list(@RequestParam(required = false) Long subjectId) {
        return Result.ok(wrongQuestionService.getWrongQuestions(subjectId));
    }

    @GetMapping("/questions")
    public Result<List<Question>> getQuestions(@RequestParam(required = false) Long subjectId) {
        return Result.ok(wrongQuestionService.getWrongQuestionList(subjectId));
    }

    @DeleteMapping("/{questionId}")
    public Result<Void> remove(@PathVariable Long questionId) {
        Long userId = com.questionbank.security.SecurityUtil.getCurrentUserId();
        wrongQuestionService.removeWrongQuestion(userId, questionId);
        return Result.ok();
    }
}
