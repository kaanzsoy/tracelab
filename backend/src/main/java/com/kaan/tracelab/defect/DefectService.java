package com.kaan.tracelab.defect;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.project.ProjectRepository;
import com.kaan.tracelab.testexecution.TestExecution;
import com.kaan.tracelab.testexecution.TestExecutionRepository;
import com.kaan.tracelab.testexecution.TestExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefectService {

    private final DefectRepository defectRepository;
    private final TestExecutionRepository testExecutionRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<DefectResponse> getDefectsByProjectId(Long projectId) {
        ensureProjectExists(projectId);

        return defectRepository
                .findByTestExecutionTestRunProjectIdOrderByIdAsc(projectId)
                .stream()
                .map(DefectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DefectResponse getDefectById(Long id) {
        Defect defect = findDefectOrThrow(id);

        return DefectResponse.from(defect);
    }

    public DefectResponse createDefect(
            Long executionId,
            DefectCreateRequest request
    ) {
        TestExecution execution =
                findTestExecutionOrThrow(executionId);

        validateExecutionFailed(execution);
        validateDefectDoesNotExist(executionId);

        Defect defect = Defect.builder()
                .title(request.title())
                .description(request.description())
                .severity(request.severity())
                .status(DefectStatus.OPEN)
                .assignedTo(request.assignedTo())
                .resolutionNotes(null)
                .testExecution(execution)
                .build();

        Defect savedDefect = defectRepository.save(defect);

        savedDefect.setDefectCode(
                String.format("BUG-%04d", savedDefect.getId())
        );

        Defect codedDefect =
                defectRepository.save(savedDefect);

        return DefectResponse.from(codedDefect);
    }

    public DefectResponse updateDefect(
            Long id,
            DefectUpdateRequest request
    ) {
        Defect defect = findDefectOrThrow(id);

        validateResolutionNotes(
                request.status(),
                request.resolutionNotes()
        );

        defect.setTitle(request.title());
        defect.setDescription(request.description());
        defect.setSeverity(request.severity());
        defect.setStatus(request.status());
        defect.setAssignedTo(request.assignedTo());
        defect.setResolutionNotes(request.resolutionNotes());

        Defect updatedDefect = defectRepository.save(defect);

        return DefectResponse.from(updatedDefect);
    }

    public void deleteDefect(Long id) {
        Defect defect = findDefectOrThrow(id);

        defectRepository.delete(defect);
    }

    private Defect findDefectOrThrow(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Defect not found with id: " + id
                        )
                );
    }

    private TestExecution findTestExecutionOrThrow(
            Long executionId
    ) {
        return testExecutionRepository.findById(executionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test execution not found with id: "
                                        + executionId
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

    private void validateExecutionFailed(
            TestExecution execution
    ) {
        if (execution.getResult() != TestExecutionResult.FAILED) {
            // PASSED, BLOCKED, NOT_RUN durumlarinda defect acilmasini reddediyoruz
            throw new IllegalArgumentException(
                    "Defect can only be created for a FAILED test execution"
            );
        }
    }

    private void validateDefectDoesNotExist(
            Long executionId
    ) {
        boolean defectExists =
                defectRepository
                        .existsByTestExecutionId(executionId);

        if (defectExists) {
            throw new IllegalArgumentException(
                    "A defect already exists for this test execution"
            );
        }
    }

    private void validateResolutionNotes(
            DefectStatus status,
            String resolutionNotes
    ) {
        boolean resolutionRequired =
                status == DefectStatus.RESOLVED
                        || status == DefectStatus.CLOSED;

        if (
                resolutionRequired
                        && (
                        resolutionNotes == null
                                || resolutionNotes.isBlank()
                )
        ) {
            throw new IllegalArgumentException(
                    "Resolution notes are required when defect status is "
                            + status
            );
        }
    }
}