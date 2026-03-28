package com.questionbank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.questionbank.common.BusinessException;
import com.questionbank.common.PageResult;
import com.questionbank.dto.QuestionDTO;
import com.questionbank.entity.Question;
import com.questionbank.mapper.QuestionMapper;
import com.questionbank.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public PageResult<Question> page(int current, int size, Long subjectId, Integer type, String keyword, Integer status) {
        Page<Question> page = new Page<>(current, size);
        questionMapper.selectPageWithDetail(page, subjectId, type, keyword, status);
        return PageResult.from(page);
    }

    public Question getById(Long id) {
        Question q = questionMapper.selectById(id);
        if (q == null) throw new BusinessException("题目不存在");
        return q;
    }

    public Question create(QuestionDTO dto) {
        Question q = new Question();
        q.setSubjectId(dto.getSubjectId());
        q.setCreatorId(SecurityUtil.getCurrentUserId());
        q.setType(dto.getType());
        q.setContent(dto.getContent());
        q.setOptions(dto.getOptions());
        q.setAnswer(dto.getAnswer());
        q.setAnalysis(dto.getAnalysis());
        q.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : 3);
        q.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        questionMapper.insert(q);
        log.info("Question created: id={}, type={}", q.getId(), q.getType());
        return q;
    }

    public Question update(Long id, QuestionDTO dto) {
        Question q = questionMapper.selectById(id);
        if (q == null) throw new BusinessException("题目不存在");
        q.setSubjectId(dto.getSubjectId());
        q.setType(dto.getType());
        q.setContent(dto.getContent());
        q.setOptions(dto.getOptions());
        q.setAnswer(dto.getAnswer());
        q.setAnalysis(dto.getAnalysis());
        if (dto.getDifficulty() != null) q.setDifficulty(dto.getDifficulty());
        if (dto.getStatus() != null) q.setStatus(dto.getStatus());
        questionMapper.updateById(q);
        log.info("Question updated: id={}", q.getId());
        return q;
    }

    public void delete(Long id) {
        Question q = questionMapper.selectById(id);
        if (q == null) throw new BusinessException("题目不存在");
        questionMapper.deleteById(id);
        log.info("Question deleted: id={}", id);
    }

    public List<Question> getPracticeQuestions(Long subjectId, int limit) {
        return questionMapper.selectPracticeQuestions(subjectId, limit);
    }
}
