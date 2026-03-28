package com.questionbank.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.questionbank.entity.User;
import com.questionbank.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 确保 admin 账号存在且密码正确
        User admin = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("Administrator");
            admin.setRole(1);
            admin.setStatus(1);
            userMapper.insert(admin);
            log.info("Admin user created: admin/123456");
        } else {
            // 更新密码确保可用
            admin.setPassword(passwordEncoder.encode("123456"));
            userMapper.updateById(admin);
            log.info("Admin password reset to: 123456");
        }

        User student = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "student"));
        if (student == null) {
            student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("123456"));
            student.setNickname("Test Student");
            student.setRole(0);
            student.setStatus(1);
            userMapper.insert(student);
            log.info("Student user created: student/123456");
        } else {
            student.setPassword(passwordEncoder.encode("123456"));
            userMapper.updateById(student);
        }
    }
}
