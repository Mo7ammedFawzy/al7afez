package com.al7afez.al7afez.dto;

public record ReportSummaryResponse(
        long totalStudents,
        long totalGroups,
        long totalLevels,
        long totalRecitations,
        long totalMistakes,
        double averageGrade
) {
}
