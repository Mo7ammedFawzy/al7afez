package com.al7afez.al7afez.dto;

import java.util.List;

public record ReportsOverviewResponse(
        ReportSummaryResponse summary,
        List<MistakeBreakdownResponse> topMistakes,
        List<ReportDimensionResponse> students,
        List<ReportDimensionResponse> groups,
        List<ReportDimensionResponse> levels,
        List<RecitationResponse> recentRecitations
) {
}
