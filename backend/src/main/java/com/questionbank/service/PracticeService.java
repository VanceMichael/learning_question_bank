package com.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.questionbank.common.BusinessException;
import com.questionbank.dto.PracticeSubmitDTO;
import com.questionbank.dto.StatsVO;
import com.questionbank.entity.*;
import com.questionbank.mapper.*;
import com.questionbank.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeRecordMapper recordMapper;
    private final PracticeDetailMapper detailMapper;
    private final QuestionMapper questionMapper;
    private final ObjectMapper objectMapper;
    private final WrongQuestionService wrongQuestionService;

    @Transactional
    public PracticeRecord submit(PracticeSubmitDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<PracticeSubmitDTO.AnswerItem> answers = dto.getAnswers();
        if (answers == null || answers.isEmpty()) {
            throw new BusinessException("答题记录不能为空");
        }

        int correctCount = 0;
        List<PracticeDetail> details = new ArrayList<>();

        for (int i = 0; i < answers.size(); i++) {
            PracticeSubmitDTO.AnswerItem item = answers.get(i);
            Question question = questionMapper.selectById(item.getQuestionId());
            if (question == null) continue;

            boolean isCorrect = checkAnswer(question, item.getUserAnswer());
            if (isCorrect) correctCount++;

            PracticeDetail detail = new PracticeDetail();
            detail.setQuestionId(item.getQuestionId());
            detail.setUserAnswer(item.getUserAnswer());
            detail.setIsCorrect(isCorrect ? 1 : 0);
            detail.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : i);
            details.add(detail);
        }

        PracticeRecord record = new PracticeRecord();
        record.setUserId(userId);
        record.setSubjectId(dto.getSubjectId());
        record.setTotalCount(details.size());
        record.setCorrectCount(correctCount);
        BigDecimal accuracy = details.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(correctCount).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(details.size()), 2, RoundingMode.HALF_UP);
        record.setAccuracy(accuracy);
        record.setStartedAt(LocalDateTime.now());
        record.setFinishedAt(LocalDateTime.now());
        recordMapper.insert(record);

        for (PracticeDetail detail : details) {
            detail.setRecordId(record.getId());
            detailMapper.insert(detail);
            
            // 如果回答错误，添加到错题本
            if (detail.getIsCorrect() == 0) {
                Question question = questionMapper.selectById(detail.getQuestionId());
                if (question != null) {
                    wrongQuestionService.addOrUpdateWrongQuestion(userId, detail.getQuestionId(), question.getSubjectId());
                }
            }
        }

        log.info("Practice submitted: userId={}, subjectId={}, accuracy={}%", userId, dto.getSubjectId(), accuracy);
        return record;
    }

    private boolean checkAnswer(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.isBlank()) return false;
        try {
            Object correctAnswer = question.getAnswer();
            String correctStr = correctAnswer instanceof String ?
                    (String) correctAnswer : objectMapper.writeValueAsString(correctAnswer);

            int type = question.getType();
            if (type == 2) {
                // 多选题：比较排序后的数组
                List<String> correct = objectMapper.readValue(correctStr, new TypeReference<>() {});
                List<String> user = objectMapper.readValue(userAnswer, new TypeReference<>() {});
                correct.sort(String::compareTo);
                user.sort(String::compareTo);
                return correct.equals(user);
            } else if (type == 3) {
                // 判断题
                return correctStr.replace("\"", "").trim()
                        .equalsIgnoreCase(userAnswer.replace("\"", "").trim());
            } else if (type == 5) {
                // 简答题：包含关键词即可
                return userAnswer.trim().length() > 0;
            } else {
                // 单选、填空
                return correctStr.replace("\"", "").trim()
                        .equalsIgnoreCase(userAnswer.replace("\"", "").trim());
            }
        } catch (Exception e) {
            log.warn("Answer check failed for question {}: {}", question.getId(), e.getMessage());
            return false;
        }
    }

    public List<PracticeRecord> getRecords(Long userId) {
        return recordMapper.selectRecentByUser(userId, 50);
    }

    public PracticeRecord getRecordDetail(Long recordId) {
        PracticeRecord record = recordMapper.selectById(recordId);
        if (record == null) throw new BusinessException("记录不存在");
        return record;
    }

    public List<PracticeDetail> getRecordDetails(Long recordId) {
        List<PracticeDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<PracticeDetail>()
                        .eq(PracticeDetail::getRecordId, recordId)
                        .orderByAsc(PracticeDetail::getSortOrder));
        for (PracticeDetail detail : details) {
            detail.setQuestion(questionMapper.selectById(detail.getQuestionId()));
        }
        return details;
    }

    public StatsVO getStats(Long userId) {
        StatsVO stats = new StatsVO();

        List<PracticeRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<PracticeRecord>().eq(PracticeRecord::getUserId, userId));

        int totalQuestions = records.stream().mapToInt(PracticeRecord::getTotalCount).sum();
        int totalCorrect = records.stream().mapToInt(PracticeRecord::getCorrectCount).sum();
        BigDecimal overallAccuracy = totalQuestions == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalCorrect).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);

        stats.setTotalPractices(records.size());
        stats.setTotalQuestions(totalQuestions);
        stats.setTotalCorrect(totalCorrect);
        stats.setOverallAccuracy(overallAccuracy);

        // 按科目统计
        List<PracticeRecord> subjectRecords = recordMapper.selectStatsBySubject(userId);
        List<StatsVO.SubjectStats> subjectStats = new ArrayList<>();
        for (PracticeRecord sr : subjectRecords) {
            StatsVO.SubjectStats ss = new StatsVO.SubjectStats();
            ss.setSubjectId(sr.getSubjectId());
            ss.setSubjectName(sr.getSubjectName());
            ss.setTotalCount(sr.getTotalCount());
            ss.setCorrectCount(sr.getCorrectCount());
            ss.setAccuracy(sr.getTotalCount() == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(sr.getCorrectCount()).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(sr.getTotalCount()), 2, RoundingMode.HALF_UP));
            subjectStats.add(ss);
        }
        stats.setSubjectStats(subjectStats);

        // 最近记录
        List<PracticeRecord> recent = recordMapper.selectRecentByUser(userId, 10);
        List<StatsVO.RecentRecord> recentRecords = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (PracticeRecord r : recent) {
            StatsVO.RecentRecord rr = new StatsVO.RecentRecord();
            rr.setId(r.getId());
            rr.setSubjectName(r.getSubjectName());
            rr.setTotalCount(r.getTotalCount());
            rr.setCorrectCount(r.getCorrectCount());
            rr.setAccuracy(r.getAccuracy());
            rr.setFinishedAt(r.getFinishedAt() != null ? r.getFinishedAt().format(fmt) : "");
            recentRecords.add(rr);
        }
        stats.setRecentRecords(recentRecords);

        return stats;
    }
}
