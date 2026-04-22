package com.al7afez.al7afez.dto;

import java.util.List;

public record ReportDimensionResponse(
        Long id,
        String name,
        String secondaryLabel,
        long recitationCount,
        long mistakeCount,
        double averageGrade,
        String latestRecitationDate,
        List<MistakeBreakdownResponse> topMistakes
) {
}
