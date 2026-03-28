package com.questionbank.controller;

import com.questionbank.common.Result;
import com.questionbank.dto.*;
import com.questionbank.security.SecurityUtil;
import com.questionbank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(authService.getUserInfo(SecurityUtil.getCurrentUserId()));
    }
}
