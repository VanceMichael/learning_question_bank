package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.questionbank.entity.WrongQuestion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    @Select("SELECT w.id, w.user_id, w.question_id, w.subject_id, w.wrong_count, w.first_wrong_at, w.last_wrong_at, " +
            "q.id as q_id, q.subject_id as q_subject_id, q.creator_id as q_creator_id, q.type as q_type, q.content as q_content, " +
            "q.options as q_options, q.answer as q_answer, q.analysis as q_analysis, q.difficulty as q_difficulty, " +
            "q.status as q_status, q.created_at as q_created_at, q.updated_at as q_updated_at, " +
            "s.name as subject_name " +
            "FROM wrong_question w " +
            "JOIN question q ON w.question_id = q.id " +
            "JOIN subject s ON w.subject_id = s.id " +
            "WHERE w.user_id = #{userId} " +
            "ORDER BY w.last_wrong_at DESC")
    List<Map<String, Object>> selectByUserIdWithDetails(@Param("userId") Long userId);

    @Select("SELECT w.id, w.user_id, w.question_id, w.subject_id, w.wrong_count, w.first_wrong_at, w.last_wrong_at, " +
            "q.id as q_id, q.subject_id as q_subject_id, q.creator_id as q_creator_id, q.type as q_type, q.content as q_content, " +
            "q.options as q_options, q.answer as q_answer, q.analysis as q_analysis, q.difficulty as q_difficulty, " +
            "q.status as q_status, q.created_at as q_created_at, q.updated_at as q_updated_at, " +
            "s.name as subject_name " +
            "FROM wrong_question w " +
            "JOIN question q ON w.question_id = q.id " +
            "JOIN subject s ON w.subject_id = s.id " +
            "WHERE w.user_id = #{userId} AND w.subject_id = #{subjectId} " +
            "ORDER BY w.last_wrong_at DESC")
    List<Map<String, Object>> selectByUserIdAndSubjectIdWithDetails(@Param("userId") Long userId, @Param("subjectId") Long subjectId);

    @Select("SELECT COUNT(*) FROM wrong_question w WHERE w.user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}