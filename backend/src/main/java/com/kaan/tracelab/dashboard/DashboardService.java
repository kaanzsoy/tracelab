package com.kaan.tracelab.dashboard;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.defect.DefectRepository;
import com.kaan.tracelab.defect.DefectSeverity;
import com.kaan.tracelab.defect.DefectStatus;
import com.kaan.tracelab.project.Project;
import com.kaan.tracelab.project.ProjectRepository;
import com.kaan.tracelab.requirement.RequirementRepository;
import com.kaan.tracelab.testcase.TestCaseRepository;
import com.kaan.tracelab.testexecution.TestExecutionRepository;
import com.kaan.tracelab.testexecution.TestExecutionResult;
import com.kaan.tracelab.testrun.TestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final RequirementRepository requirementRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestRunRepository testRunRepository;
    private final TestExecutionRepository testExecutionRepository;
    private final DefectRepository defectRepository;

    public DashboardResponse getProjectDashboard(Long projectId) {
        Project project = findProjectOrThrow(projectId);

        long totalRequirements =
                requirementRepository.countByProjectId(projectId);

        long coveredRequirements =
                requirementRepository
                        .countCoveredRequirementsByProjectId(projectId);

        double requirementCoverageRate = calculatePercentage(
                coveredRequirements,
                totalRequirements
        );

        long totalTestCases =
                testCaseRepository
                        .countByRequirementProjectId(projectId);

        long totalTestRuns =
                testRunRepository.countByProjectId(projectId);

        long totalExecutions =
                testExecutionRepository
                        .countByTestRunProjectId(projectId);

        long passedExecutions = countExecutionsByResult(
                projectId,
                TestExecutionResult.PASSED
        );

        long failedExecutions = countExecutionsByResult(
                projectId,
                TestExecutionResult.FAILED
        );

        long blockedExecutions = countExecutionsByResult(
                projectId,
                TestExecutionResult.BLOCKED
        );

        long notRunExecutions = countExecutionsByResult(
                projectId,
                TestExecutionResult.NOT_RUN
        );

        long executedTestCount =
                passedExecutions
                        + failedExecutions
                        + blockedExecutions;

        double passRate = calculatePercentage(
                passedExecutions,
                executedTestCount
        );

        long totalDefects =
                defectRepository
                        .countByTestExecutionTestRunProjectId(projectId);

        long activeDefects =
                defectRepository
                        .countByTestExecutionTestRunProjectIdAndStatusIn(
                                projectId,
                                List.of(
                                        DefectStatus.OPEN,
                                        DefectStatus.IN_PROGRESS,
                                        DefectStatus.REOPENED
                                )
                        );

        DefectSeveritySummary defectsBySeverity =
                createDefectSeveritySummary(projectId);

        return new DashboardResponse(
                project.getId(),
                project.getName(),

                totalRequirements,
                coveredRequirements,
                requirementCoverageRate,

                totalTestCases,
                totalTestRuns,

                totalExecutions,
                passedExecutions,
                failedExecutions,
                blockedExecutions,
                notRunExecutions,
                passRate,

                totalDefects,
                activeDefects,
                defectsBySeverity
        );
    }

    private long countExecutionsByResult(
            Long projectId,
            TestExecutionResult result
    ) {
        return testExecutionRepository
                .countByTestRunProjectIdAndResult(
                        projectId,
                        result
                );
    }

    private DefectSeveritySummary createDefectSeveritySummary(
            Long projectId
    ) {
        long low = countDefectsBySeverity(
                projectId,
                DefectSeverity.LOW
        );

        long medium = countDefectsBySeverity(
                projectId,
                DefectSeverity.MEDIUM
        );

        long high = countDefectsBySeverity(
                projectId,
                DefectSeverity.HIGH
        );

        long critical = countDefectsBySeverity(
                projectId,
                DefectSeverity.CRITICAL
        );

        return new DefectSeveritySummary(
                low,
                medium,
                high,
                critical
        );
    }

    private long countDefectsBySeverity(
            Long projectId,
            DefectSeverity severity
    ) {
        return defectRepository
                .countByTestExecutionTestRunProjectIdAndSeverity(
                        projectId,
                        severity
                );
    }

    private double calculatePercentage(
            long numerator,
            long denominator
    ) {
        if (denominator == 0) {
            return 0.0;
        }

        double percentage =
                ((double) numerator / denominator) * 100;

        return Math.round(percentage * 100.0) / 100.0;
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + projectId
                        )
                );
    }
}