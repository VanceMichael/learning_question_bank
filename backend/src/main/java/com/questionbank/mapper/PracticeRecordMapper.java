package com.questionbank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.questionbank.entity.PracticeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PracticeRecordMapper extends BaseMapper<PracticeRecord> {

    @Select("SELECT pr.*, s.name as subject_name FROM practice_record pr " +
            "LEFT JOIN subject s ON pr.subject_id = s.id " +
            "WHERE pr.user_id = #{userId} ORDER BY pr.started_at DESC LIMIT #{limit}")
    List<PracticeRecord> selectRecentByUser(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT s.id as subject_id, s.name as subject_name, " +
            "COALESCE(SUM(pr.total_count),0) as total_count, " +
            "COALESCE(SUM(pr.correct_count),0) as correct_count " +
            "FROM subject s LEFT JOIN practice_record pr ON s.id = pr.subject_id AND pr.user_id = #{userId} " +
            "WHERE s.status = 1 GROUP BY s.id, s.name ORDER BY s.sort_order")
    List<PracticeRecord> selectStatsBySubject(@Param("userId") Long userId);
}
