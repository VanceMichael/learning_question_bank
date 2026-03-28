package com.questionbank.controller;

import com.questionbank.common.Result;
import com.questionbank.dto.SubjectDTO;
import com.questionbank.entity.Subject;
import com.questionbank.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/subjects")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public Result<Subject> create(@Valid @RequestBody SubjectDTO dto) {
        return Result.ok(subjectService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Subject> update(@PathVariable Long id, @Valid @RequestBody SubjectDTO dto) {
        return Result.ok(subjectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return Result.ok();
    }
}
