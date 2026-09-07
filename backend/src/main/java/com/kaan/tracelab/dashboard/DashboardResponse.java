package com.kaan.tracelab.dashboard;

public record DashboardResponse(
        Long projectId,
        String projectName,

        long totalRequirements,
        long coveredRequirements,
        double requirementCoverageRate,

        long totalTestCases,
        long totalTestRuns,

        long totalExecutions,
        long passedExecutions,
        long failedExecutions,
        long blockedExecutions,
        long notRunExecutions,
        double passRate,

        long totalDefects,
        long activeDefects,
        DefectSeveritySummary defectsBySeverity
) {
}