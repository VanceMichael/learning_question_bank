package com.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.questionbank.common.BusinessException;
import com.questionbank.entity.WrongQuestion;
import com.questionbank.mapper.WrongQuestionMapper;
import com.questionbank.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WrongQuestionService {

    private final WrongQuestionMapper wrongQuestionMapper;

    @Transactional
    public void addOrUpdateWrongQuestion(Long userId, Long questionId, Long subjectId) {
        LambdaQueryWrapper<WrongQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getQuestionId, questionId);

        WrongQuestion existing = wrongQuestionMapper.selectOne(queryWrapper);

        if (existing != null) {
            // 更新错误次数
            LambdaUpdateWrapper<WrongQuestion> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WrongQuestion::getId, existing.getId())
                    .setSql("wrong_count = wrong_count + 1")
                    .set(WrongQuestion::getLastWrongAt, LocalDateTime.now());
            wrongQuestionMapper.update(null, updateWrapper);
            log.info("Updated wrong question: userId={}, questionId={}, wrongCount={}", userId, questionId, existing.getWrongCount() + 1);
        } else {
            // 新增错题记录
            WrongQuestion wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setSubjectId(subjectId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setFirstWrongAt(LocalDateTime.now());
            wrongQuestion.setLastWrongAt(LocalDateTime.now());
            wrongQuestionMapper.insert(wrongQuestion);
            log.info("Added new wrong question: userId={}, questionId={}", userId, questionId);
        }
    }

    @Transactional
    public void removeWrongQuestion(Long questionId) {
        Long userId = SecurityUtil.getCurrentUserId();
        LambdaQueryWrapper<WrongQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getQuestionId, questionId);

        WrongQuestion wrongQuestion = wrongQuestionMapper.selectOne(queryWrapper);
        if (wrongQuestion == null) {
            throw new BusinessException("该错题不存在");
        }

        wrongQuestionMapper.deleteById(wrongQuestion.getId());
        log.info("Removed wrong question: userId={}, questionId={}", userId, questionId);
    }

    public List<WrongQuestion> getWrongQuestions(Long subjectId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (subjectId != null) {
            return wrongQuestionMapper.selectByUserIdAndSubjectIdWithDetails(userId, subjectId);
        } else {
            return wrongQuestionMapper.selectByUserIdWithDetails(userId);
        }
    }

    public int countWrongQuestions() {
        Long userId = SecurityUtil.getCurrentUserId();
        return wrongQuestionMapper.countByUserId(userId);
    }
}