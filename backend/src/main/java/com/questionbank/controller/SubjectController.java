package com.questionbank.controller;

import com.questionbank.common.Result;
import com.questionbank.entity.Subject;
import com.questionbank.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public Result<List<Subject>> list(@RequestParam(required = false, defaultValue = "false") boolean all) {
        return Result.ok(all ? subjectService.listAll() : subjectService.listEnabled());
    }
}
