package com.questionbank.controller;

import com.questionbank.common.Result;
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
    public Result<List<WrongQuestion>> getWrongQuestions(@RequestParam(required = false) Long subjectId) {
        List<WrongQuestion> wrongQuestions = wrongQuestionService.getWrongQuestions(subjectId);
        return Result.ok(wrongQuestions);
    }

    @DeleteMapping("/{questionId}")
    public Result<Void> removeWrongQuestion(@PathVariable Long questionId) {
        wrongQuestionService.removeWrongQuestion(questionId);
        return Result.ok();
    }

    @GetMapping("/count")
    public Result<Integer> countWrongQuestions() {
        int count = wrongQuestionService.countWrongQuestions();
        return Result.ok(count);
    }
}