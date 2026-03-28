package com.questionbank.controller;

import com.questionbank.common.PageResult;
import com.questionbank.common.Result;
import com.questionbank.entity.OperationLog;
import com.questionbank.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final OperationLogService logService;

    @GetMapping
    public Result<PageResult<OperationLog>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String module) {
        return Result.ok(logService.page(current, size, module));
    }
}
