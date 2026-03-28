package com.questionbank.controller;

import com.questionbank.common.Result;
import com.questionbank.dto.PracticeSubmitDTO;
import com.questionbank.dto.StatsVO;
import com.questionbank.entity.PracticeDetail;
import com.questionbank.entity.PracticeRecord;
import com.questionbank.security.SecurityUtil;
import com.questionbank.service.PracticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practices")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping("/submit")
    public Result<PracticeRecord> submit(@Valid @RequestBody PracticeSubmitDTO dto) {
        return Result.ok(practiceService.submit(dto));
    }

    @GetMapping("/records")
    public Result<List<PracticeRecord>> records() {
        return Result.ok(practiceService.getRecords(SecurityUtil.getCurrentUserId()));
    }

    @GetMapping("/records/{id}")
    public Result<PracticeRecord> recordDetail(@PathVariable Long id) {
        return Result.ok(practiceService.getRecordDetail(id));
    }

    @GetMapping("/records/{id}/details")
    public Result<List<PracticeDetail>> recordDetails(@PathVariable Long id) {
        return Result.ok(practiceService.getRecordDetails(id));
    }

    @GetMapping("/stats")
    public Result<StatsVO> stats() {
        return Result.ok(practiceService.getStats(SecurityUtil.getCurrentUserId()));
    }
}
