package com.kaan.tracelab.testrun;

import java.time.LocalDateTime;

public record TestRunResponse(
        Long id,
        String runCode,
        String name,
        String description,
        TestEnvironment environment,
        TestRunStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Long projectId,
        String projectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TestRunResponse from(TestRun testRun) {
        return new TestRunResponse(
                testRun.getId(),
                testRun.getRunCode(),
                testRun.getName(),
                testRun.getDescription(),
                testRun.getEnvironment(),
                testRun.getStatus(),
                testRun.getStartedAt(),
                testRun.getCompletedAt(),
                testRun.getProject().getId(),
                testRun.getProject().getName(),
                testRun.getCreatedAt(),
                testRun.getUpdatedAt()
        );
    }
}