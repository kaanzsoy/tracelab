package com.kaan.tracelab.defect;

import java.time.LocalDateTime;

// tum traceability zincirini response'a koyariz:
/*
BUG-0001
→ EXEC-0002
→ RUN-0003
→ TC-0002
→ REQ-0001
→ Project 1
 */

public record DefectResponse(
        Long id,
        String defectCode,
        String title,
        String description,
        DefectSeverity severity,
        DefectStatus status,
        String assignedTo,
        String resolutionNotes,

        Long testExecutionId,
        String executionCode,

        Long testRunId,
        String testRunCode,

        Long testCaseId,
        String testCaseCode,
        String testCaseTitle,

        Long requirementId,
        String requirementCode,

        Long projectId,
        String projectName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DefectResponse from(Defect defect) {
        return new DefectResponse(
                defect.getId(),
                defect.getDefectCode(),
                defect.getTitle(),
                defect.getDescription(),
                defect.getSeverity(),
                defect.getStatus(),
                defect.getAssignedTo(),
                defect.getResolutionNotes(),

                defect.getTestExecution().getId(),
                defect.getTestExecution().getExecutionCode(),

                defect.getTestExecution()
                        .getTestRun()
                        .getId(),

                defect.getTestExecution()
                        .getTestRun()
                        .getRunCode(),

                defect.getTestExecution()
                        .getTestCase()
                        .getId(),

                defect.getTestExecution()
                        .getTestCase()
                        .getTestCaseCode(),

                defect.getTestExecution()
                        .getTestCase()
                        .getTitle(),

                defect.getTestExecution()
                        .getTestCase()
                        .getRequirement()
                        .getId(),

                defect.getTestExecution()
                        .getTestCase()
                        .getRequirement()
                        .getRequirementCode(),

                defect.getTestExecution()
                        .getTestRun()
                        .getProject()
                        .getId(),

                defect.getTestExecution()
                        .getTestRun()
                        .getProject()
                        .getName(),

                defect.getCreatedAt(),
                defect.getUpdatedAt()
        );
    }
}