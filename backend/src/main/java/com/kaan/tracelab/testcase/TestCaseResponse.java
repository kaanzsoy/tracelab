package com.kaan.tracelab.testcase;

import java.time.LocalDateTime;

public record TestCaseResponse(
        Long id,
        String testCaseCode,
        String title,
        String preconditions,
        String testSteps,
        String expectedResult,
        TestCaseType type,
        TestCasePriority priority,
        TestCaseStatus status,
        Long requirementId,
        String requirementCode,
        String requirementTitle,
        Long projectId,
        String projectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TestCaseResponse from(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getId(),
                testCase.getTestCaseCode(),
                testCase.getTitle(),
                testCase.getPreconditions(),
                testCase.getTestSteps(),
                testCase.getExpectedResult(),
                testCase.getType(),
                testCase.getPriority(),
                testCase.getStatus(),
                testCase.getRequirement().getId(),
                testCase.getRequirement().getRequirementCode(),
                testCase.getRequirement().getTitle(),
                testCase.getRequirement().getProject().getId(),
                testCase.getRequirement().getProject().getName(),
                testCase.getCreatedAt(),
                testCase.getUpdatedAt()
        );
    }
}