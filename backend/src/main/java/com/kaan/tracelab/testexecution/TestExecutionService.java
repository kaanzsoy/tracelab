package com.kaan.tracelab.testexecution;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.testcase.TestCase;
import com.kaan.tracelab.testcase.TestCaseRepository;
import com.kaan.tracelab.testrun.TestRun;
import com.kaan.tracelab.testrun.TestRunRepository;
import com.kaan.tracelab.testrun.TestRunStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestExecutionService {

    private final TestExecutionRepository testExecutionRepository;
    private final TestRunRepository testRunRepository;
    private final TestCaseRepository testCaseRepository;

    @Transactional(readOnly = true)
    public List<TestExecutionResponse> getExecutionsByTestRunId(
            Long testRunId
    ) {
        ensureTestRunExists(testRunId);

        return testExecutionRepository
                .findByTestRunIdOrderByIdAsc(testRunId)
                .stream()
                .map(TestExecutionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestExecutionResponse getExecutionById(Long id) {
        TestExecution execution = findExecutionOrThrow(id);

        return TestExecutionResponse.from(execution);
    }

    public TestExecutionResponse createExecution(
            Long testRunId,
            TestExecutionCreateRequest request
    ) {
        TestRun testRun = findTestRunOrThrow(testRunId);

        TestCase testCase =
                findTestCaseOrThrow(request.testCaseId());

        validateTestRunCanBeModified(testRun);

        validateSameProject(testRun, testCase);

        validateExecutionDoesNotExist(
                testRunId,
                request.testCaseId()
        );

        TestExecution execution = TestExecution.builder()
                .testRun(testRun)
                .testCase(testCase)
                .result(TestExecutionResult.NOT_RUN)
                .actualResult(null)
                .notes(null)
                .executedAt(null)
                .build();

        TestExecution savedExecution =
                testExecutionRepository.save(execution);

        savedExecution.setExecutionCode(
                String.format(
                        "EXEC-%04d",
                        savedExecution.getId()
                )
        );

        TestExecution codedExecution =
                testExecutionRepository.save(savedExecution);

        return TestExecutionResponse.from(codedExecution);
    }

    public TestExecutionResponse updateExecution(
            Long id,
            TestExecutionUpdateRequest request
    ) {
        TestExecution execution = findExecutionOrThrow(id);

        validateTestRunCanBeModified(execution.getTestRun());

        validateExecutionResult(request);

        execution.setResult(request.result());
        execution.setActualResult(request.actualResult());
        execution.setNotes(request.notes());
        execution.setExecutedAt(request.executedAt());

        TestExecution updatedExecution =
                testExecutionRepository.save(execution);

        return TestExecutionResponse.from(updatedExecution);
    }

    public void deleteExecution(Long id) {
        TestExecution execution = findExecutionOrThrow(id);

        validateTestRunCanBeModified(execution.getTestRun());

        testExecutionRepository.delete(execution);
    }

    private TestExecution findExecutionOrThrow(Long id) {
        return testExecutionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test execution not found with id: " + id
                        )
                );
    }

    private TestRun findTestRunOrThrow(Long testRunId) {
        return testRunRepository.findById(testRunId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test run not found with id: " + testRunId
                        )
                );
    }

    private TestCase findTestCaseOrThrow(Long testCaseId) {
        return testCaseRepository.findById(testCaseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test case not found with id: " + testCaseId
                        )
                );
    }

    private void ensureTestRunExists(Long testRunId) {
        if (!testRunRepository.existsById(testRunId)) {
            throw new ResourceNotFoundException(
                    "Test run not found with id: " + testRunId
            );
        }
    }

    private void validateExecutionDoesNotExist(
            Long testRunId,
            Long testCaseId
    ) {
        boolean executionExists =
                testExecutionRepository
                        .existsByTestRunIdAndTestCaseId(
                                testRunId,
                                testCaseId
                        );

        if (executionExists) {
            throw new IllegalArgumentException(
                    "Test case is already included in this test run"
            );
        }
    }

    private void validateSameProject(
            TestRun testRun,
            TestCase testCase
    ) {
        Long testRunProjectId =
                testRun.getProject().getId();

        Long testCaseProjectId =
                testCase.getRequirement()
                        .getProject()
                        .getId();

        if (!testRunProjectId.equals(testCaseProjectId)) {
            throw new IllegalArgumentException(
                    "Test case and test run must belong to the same project"
            );
        }
    }

    private void validateTestRunCanBeModified(
            TestRun testRun
    ) {
        if (testRun.getStatus() == TestRunStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed test runs cannot be modified"
            );
        }

        if (testRun.getStatus() == TestRunStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled test runs cannot be modified"
            );
        }
    }

    private void validateExecutionResult(
            TestExecutionUpdateRequest request
    ) {
        if (
                request.result() != TestExecutionResult.NOT_RUN
                        && request.executedAt() == null
        ) {
            throw new IllegalArgumentException(
                    "Executed date is required when result is not NOT_RUN"
            );
        }

        if (
                request.result() == TestExecutionResult.NOT_RUN
                        && request.executedAt() != null
        ) {
            throw new IllegalArgumentException(
                    "Executed date must be empty when result is NOT_RUN"
            );
        }

        if (
                request.result() == TestExecutionResult.FAILED
                        && (
                        request.actualResult() == null
                                || request.actualResult().isBlank()
                )
        ) {
            throw new IllegalArgumentException(
                    "Actual result is required when execution result is FAILED"
            );
        }
    }
}