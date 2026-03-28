package com.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.questionbank.common.BusinessException;
import com.questionbank.dto.SubjectDTO;
import com.questionbank.entity.Subject;
import com.questionbank.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectMapper subjectMapper;

    public List<Subject> listAll() {
        return subjectMapper.selectList(
                new LambdaQueryWrapper<Subject>().orderByAsc(Subject::getSortOrder));
    }

    public List<Subject> listEnabled() {
        return subjectMapper.selectList(
                new LambdaQueryWrapper<Subject>()
                        .eq(Subject::getStatus, 1)
                        .orderByAsc(Subject::getSortOrder));
    }

    public Subject create(SubjectDTO dto) {
        Long count = subjectMapper.selectCount(
                new LambdaQueryWrapper<Subject>().eq(Subject::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException("科目名称已存在");
        }
        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setIcon(dto.getIcon());
        subject.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        subject.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        subjectMapper.insert(subject);
        log.info("Subject created: {}", subject.getName());
        return subject;
    }

    public Subject update(Long id, SubjectDTO dto) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException("科目不存在");
        }
        Long count = subjectMapper.selectCount(
                new LambdaQueryWrapper<Subject>()
                        .eq(Subject::getName, dto.getName())
                        .ne(Subject::getId, id));
        if (count > 0) {
            throw new BusinessException("科目名称已存在");
        }
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setIcon(dto.getIcon());
        if (dto.getSortOrder() != null) subject.setSortOrder(dto.getSortOrder());
        if (dto.getStatus() != null) subject.setStatus(dto.getStatus());
        subjectMapper.updateById(subject);
        log.info("Subject updated: {}", subject.getName());
        return subject;
    }

    public void delete(Long id) {
        Subject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException("科目不存在");
        }
        subjectMapper.deleteById(id);
        log.info("Subject deleted: {}", subject.getName());
    }
}
