package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.questionbank.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    IPage<Question> selectPageWithDetail(Page<Question> page,
                                          @Param("subjectId") Long subjectId,
                                          @Param("type") Integer type,
                                          @Param("keyword") String keyword,
                                          @Param("status") Integer status);

    List<Question> selectPracticeQuestions(@Param("subjectId") Long subjectId, @Param("limit") int limit);
}
