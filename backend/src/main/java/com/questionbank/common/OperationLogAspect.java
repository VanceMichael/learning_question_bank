package com.questionbank.common;

import com.questionbank.security.LoginUser;
import com.questionbank.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService logService;

    // 模块映射
    private static final Map<String, String> MODULE_MAP = new HashMap<>() {{
        put("AdminSubjectController", "科目管理");
        put("AdminQuestionController", "题目管理");
        put("AdminUserController", "用户管理");
        put("QuestionController", "题目");
        put("PracticeController", "练习");
        put("AuthController", "认证");
    }};

    // 操作映射
    private static final Map<String, String> ACTION_MAP = new HashMap<>() {{
        put("create", "新增");
        put("update", "修改");
        put("delete", "删除");
        put("updateStatus", "更新状态");
        put("login", "登录");
        put("register", "注册");
        put("submit", "提交");
    }};

    @Pointcut("execution(* com.questionbank.controller.Admin*.*(..)) || " +
              "execution(* com.questionbank.controller.AuthController.login(..)) || " +
              "execution(* com.questionbank.controller.AuthController.register(..))")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();

        try {
            String className = point.getTarget().getClass().getSimpleName();
            String methodName = point.getSignature().getName();

            // 获取模块名
            String module = MODULE_MAP.getOrDefault(className, className.replace("Controller", ""));

            // 获取操作名
            String action = ACTION_MAP.entrySet().stream()
                    .filter(e -> methodName.toLowerCase().contains(e.getKey().toLowerCase()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(methodName);

            // 获取当前用户
            Long userId = null;
            String username = "";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof LoginUser loginUser) {
                userId = loginUser.getUserId();
                username = loginUser.getUsername();
            }

            // 获取IP
            String ip = getClientIp();

            // 构建详情
            String detail = String.format("%s - %s", module, action);

            // 异步记录日志
            logService.log(userId, username, module, action, detail, ip);

        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        return result;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                // 多个代理时取第一个
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("获取IP失败: {}", e.getMessage());
        }
        return "";
    }
}
