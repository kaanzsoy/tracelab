package com.kaan.tracelab.testexecution;

import java.time.LocalDateTime;

/* 
Project
└── Test Run
    └── Test Execution
        └── Test Case
            └── Requirement
*/

public record TestExecutionResponse(
        Long id,
        String executionCode,

        Long testRunId,
        String testRunCode,
        String testRunName,

        Long testCaseId,
        String testCaseCode,
        String testCaseTitle,

        Long requirementId,
        String requirementCode,

        Long projectId,
        String projectName,

        TestExecutionResult result,
        String actualResult,
        String notes,
        LocalDateTime executedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TestExecutionResponse from(
            TestExecution testExecution
    ) {
        return new TestExecutionResponse(
                testExecution.getId(),
                testExecution.getExecutionCode(),

                testExecution.getTestRun().getId(),
                testExecution.getTestRun().getRunCode(),
                testExecution.getTestRun().getName(),

                testExecution.getTestCase().getId(),
                testExecution.getTestCase().getTestCaseCode(),
                testExecution.getTestCase().getTitle(),

                testExecution.getTestCase()
                        .getRequirement()
                        .getId(),

                testExecution.getTestCase()
                        .getRequirement()
                        .getRequirementCode(),

                testExecution.getTestRun()
                        .getProject()
                        .getId(),

                testExecution.getTestRun()
                        .getProject()
                        .getName(),

                testExecution.getResult(),
                testExecution.getActualResult(),
                testExecution.getNotes(),
                testExecution.getExecutedAt(),
                testExecution.getCreatedAt(),
                testExecution.getUpdatedAt()
        );
    }
}