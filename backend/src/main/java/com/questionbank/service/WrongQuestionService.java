package com.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.questionbank.common.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questionbank.entity.Question;
import com.questionbank.entity.WrongQuestion;
import com.questionbank.mapper.WrongQuestionMapper;
import com.questionbank.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WrongQuestionService {

    private final WrongQuestionMapper wrongQuestionMapper;
    private final ObjectMapper objectMapper;

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
        List<Map<String, Object>> results;
        if (subjectId != null) {
            results = wrongQuestionMapper.selectByUserIdAndSubjectIdWithDetails(userId, subjectId);
        } else {
            results = wrongQuestionMapper.selectByUserIdWithDetails(userId);
        }
        
        return results.stream().map(this::convertToWrongQuestion).toList();
    }
    
    private WrongQuestion convertToWrongQuestion(Map<String, Object> map) {
        WrongQuestion wq = new WrongQuestion();
        wq.setId((Long) map.get("id"));
        wq.setUserId((Long) map.get("user_id"));
        wq.setQuestionId((Long) map.get("question_id"));
        wq.setSubjectId((Long) map.get("subject_id"));
        wq.setWrongCount((Integer) map.get("wrong_count"));
        wq.setFirstWrongAt((LocalDateTime) map.get("first_wrong_at"));
        wq.setLastWrongAt((LocalDateTime) map.get("last_wrong_at"));
        wq.setSubjectName((String) map.get("subject_name"));
        
        Question question = new Question();
        question.setId((Long) map.get("q_id"));
        question.setSubjectId((Long) map.get("q_subject_id"));
        question.setCreatorId((Long) map.get("q_creator_id"));
        question.setType((Integer) map.get("q_type"));
        question.setContent((String) map.get("q_content"));
        question.setOptions(convertToOptionsList((String) map.get("q_options")));
        question.setAnswer(convertToAnswer((String) map.get("q_answer")));
        question.setAnalysis((String) map.get("q_analysis"));
        question.setDifficulty((Integer) map.get("q_difficulty"));
        question.setStatus((Integer) map.get("q_status"));
        question.setCreatedAt((LocalDateTime) map.get("q_created_at"));
        question.setUpdatedAt((LocalDateTime) map.get("q_updated_at"));
        
        wq.setQuestion(question);
        return wq;
    }
    
    private List<String> convertToOptionsList(String options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(
                options, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
        } catch (Exception e) {
            log.error("Failed to convert options: {}", options, e);
            return null;
        }
    }
    
    private Object convertToAnswer(String answer) {
        if (answer == null || answer.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(answer, Object.class);
        } catch (Exception e) {
            log.error("Failed to convert answer: {}", answer, e);
            return answer;
        }
    }

    public int countWrongQuestions() {
        Long userId = SecurityUtil.getCurrentUserId();
        return wrongQuestionMapper.countByUserId(userId);
    }
}