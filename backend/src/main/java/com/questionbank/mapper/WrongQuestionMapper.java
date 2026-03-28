package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.questionbank.entity.WrongQuestion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    @Select("SELECT w.*, q.*, s.name as subject_name " +
            "FROM wrong_question w " +
            "JOIN question q ON w.question_id = q.id " +
            "JOIN subject s ON w.subject_id = s.id " +
            "WHERE w.user_id = #{userId} " +
            "ORDER BY w.last_wrong_at DESC")
    List<WrongQuestion> selectByUserIdWithDetails(@Param("userId") Long userId);

    @Select("SELECT w.*, q.*, s.name as subject_name " +
            "FROM wrong_question w " +
            "JOIN question q ON w.question_id = q.id " +
            "JOIN subject s ON w.subject_id = s.id " +
            "WHERE w.user_id = #{userId} AND w.subject_id = #{subjectId} " +
            "ORDER BY w.last_wrong_at DESC")
    List<WrongQuestion> selectByUserIdAndSubjectIdWithDetails(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    @Select("SELECT COUNT(*) FROM wrong_question w WHERE w.user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}