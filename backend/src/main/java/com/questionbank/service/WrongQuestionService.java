package com.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.questionbank.entity.Question;
import com.questionbank.entity.WrongQuestion;
import com.questionbank.mapper.QuestionMapper;
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
    private final QuestionMapper questionMapper;

    @Transactional
    public void addWrongQuestion(Long userId, Long questionId, Long subjectId) {
        WrongQuestion existing = wrongQuestionMapper.selectOne(
                new LambdaQueryWrapper<WrongQuestion>()
                        .eq(WrongQuestion::getUserId, userId)
                        .eq(WrongQuestion::getQuestionId, questionId)
        );

        if (existing != null) {
            wrongQuestionMapper.update(null,
                    new LambdaUpdateWrapper<WrongQuestion>()
                            .eq(WrongQuestion::getId, existing.getId())
                            .set(WrongQuestion::getWrongCount, existing.getWrongCount() + 1)
                            .set(WrongQuestion::getLastWrongAt, LocalDateTime.now())
            );
            log.info("Updated wrong question: userId={}, questionId={}, wrongCount={}", userId, questionId, existing.getWrongCount() + 1);
        } else {
            WrongQuestion wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setSubjectId(subjectId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setLastWrongAt(LocalDateTime.now());
            wrongQuestionMapper.insert(wrongQuestion);
            log.info("Added new wrong question: userId={}, questionId={}", userId, questionId);
        }
    }

    @Transactional
    public void removeWrongQuestion(Long userId, Long questionId) {
        wrongQuestionMapper.delete(
                new LambdaQueryWrapper<WrongQuestion>()
                        .eq(WrongQuestion::getUserId, userId)
                        .eq(WrongQuestion::getQuestionId, questionId)
        );
        log.info("Removed wrong question: userId={}, questionId={}", userId, questionId);
    }

    public List<WrongQuestion> getWrongQuestions(Long subjectId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (subjectId != null) {
            return wrongQuestionMapper.selectByUserAndSubject(userId, subjectId);
        } else {
            return wrongQuestionMapper.selectByUser(userId);
        }
    }

    public List<Question> getWrongQuestionList(Long subjectId) {
        List<WrongQuestion> wrongQuestions = getWrongQuestions(subjectId);
        return wrongQuestions.stream()
                .map(WrongQuestion::getQuestion)
                .toList();
    }
}
