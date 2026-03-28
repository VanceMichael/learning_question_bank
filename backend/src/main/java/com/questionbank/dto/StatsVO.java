package com.questionbank.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StatsVO {
    private int totalPractices;
    private int totalQuestions;
    private int totalCorrect;
    private BigDecimal overallAccuracy;
    private List<SubjectStats> subjectStats;
    private List<RecentRecord> recentRecords;

    @Data
    public static class SubjectStats {
        private Long subjectId;
        private String subjectName;
        private int totalCount;
        private int correctCount;
        private BigDecimal accuracy;
    }

    @Data
    public static class RecentRecord {
        private Long id;
        private String subjectName;
        private int totalCount;
        private int correctCount;
        private BigDecimal accuracy;
        private String finishedAt;
    }
}
