package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.questionbank.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
    List<WrongQuestion> selectByUserAndSubject(@Param("userId") Long userId, @Param("subjectId") Long subjectId);
    List<WrongQuestion> selectByUser(@Param("userId") Long userId);
}
