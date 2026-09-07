package com.kaan.tracelab.testrun;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.project.Project;
import com.kaan.tracelab.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestRunService {

    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<TestRunResponse> getTestRunsByProjectId(Long projectId) {
        ensureProjectExists(projectId);

        return testRunRepository.findByProjectIdOrderByIdAsc(projectId)
                .stream()
                .map(TestRunResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestRunResponse getTestRunById(Long id) {
        TestRun testRun = findTestRunOrThrow(id);
        return TestRunResponse.from(testRun);
    }

    public TestRunResponse createTestRun(
            Long projectId,
            TestRunRequest request
    ) {
        Project project = findProjectOrThrow(projectId);

        validateDates(request);

        TestRun testRun = TestRun.builder()
                .name(request.name())
                .description(request.description())
                .environment(request.environment())
                .status(request.status())
                .startedAt(request.startedAt())
                .completedAt(request.completedAt())
                .project(project)
                .build();

        TestRun savedTestRun = testRunRepository.save(testRun);

        savedTestRun.setRunCode(
                String.format("RUN-%04d", savedTestRun.getId())
        );

        TestRun codedTestRun = testRunRepository.save(savedTestRun);

        return TestRunResponse.from(codedTestRun);
    }

    public TestRunResponse updateTestRun(
            Long id,
            TestRunRequest request
    ) {
        validateDates(request);

        TestRun testRun = findTestRunOrThrow(id);

        testRun.setName(request.name());
        testRun.setDescription(request.description());
        testRun.setEnvironment(request.environment());
        testRun.setStatus(request.status());
        testRun.setStartedAt(request.startedAt());
        testRun.setCompletedAt(request.completedAt());

        TestRun updatedTestRun = testRunRepository.save(testRun);

        return TestRunResponse.from(updatedTestRun);
    }

    public void deleteTestRun(Long id) {
        TestRun testRun = findTestRunOrThrow(id);
        testRunRepository.delete(testRun);
    }

    private TestRun findTestRunOrThrow(Long id) {
        return testRunRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test run not found with id: " + id
                        )
                );
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );
    }

    private void ensureProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }
    }

    private void validateDates(TestRunRequest request) {
        if (
                request.startedAt() != null
                        && request.completedAt() != null
                        && request.completedAt().isBefore(request.startedAt())
        ) {
            throw new IllegalArgumentException(
                    "Completed date cannot be before started date"
            );
        }

        if (
                request.status() == TestRunStatus.COMPLETED
                        && request.completedAt() == null
        ) {
            throw new IllegalArgumentException(
                    "Completed date is required when test run status is COMPLETED"
            );
        }
    }
}