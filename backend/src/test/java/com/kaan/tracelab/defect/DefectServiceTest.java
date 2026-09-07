package com.kaan.tracelab.defect;

import com.kaan.tracelab.project.Project;
import com.kaan.tracelab.project.ProjectRepository;
import com.kaan.tracelab.requirement.Requirement;
import com.kaan.tracelab.testcase.TestCase;
import com.kaan.tracelab.testexecution.TestExecution;
import com.kaan.tracelab.testexecution.TestExecutionRepository;
import com.kaan.tracelab.testexecution.TestExecutionResult;
import com.kaan.tracelab.testrun.TestRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefectServiceTest {

    @Mock
    private DefectRepository defectRepository;

    @Mock
    private TestExecutionRepository testExecutionRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private DefectService defectService;

    private TestExecution failedExecution;

    @BeforeEach
    void setUp() {
        Project project = Project.builder()
                .id(1L)
                .name("TraceLab Core Backend")
                .build();

        Requirement requirement = Requirement.builder()
                .id(1L)
                .requirementCode("REQ-0001")
                .title("Authentication")
                .project(project)
                .build();

        TestCase testCase = TestCase.builder()
                .id(1L)
                .testCaseCode("TC-0001")
                .title("Valid login")
                .requirement(requirement)
                .build();

        TestRun testRun = TestRun.builder()
                .id(1L)
                .runCode("RUN-0001")
                .name("Authentication Run")
                .project(project)
                .build();

        failedExecution = TestExecution.builder()
                .id(1L)
                .executionCode("EXEC-0001")
                .testRun(testRun)
                .testCase(testCase)
                .result(TestExecutionResult.FAILED)
                .actualResult("HTTP 500 returned")
                .build();
    }

    @Test
    void shouldCreateDefectForFailedExecution() {
        when(testExecutionRepository.findById(1L))
                .thenReturn(Optional.of(failedExecution));

        when(defectRepository.existsByTestExecutionId(1L))
                .thenReturn(false);

        when(defectRepository.save(any(Defect.class)))
                .thenAnswer(invocation -> {
                    Defect defect = invocation.getArgument(0);

                    if (defect.getId() == null) {
                        defect.setId(1L);
                    }

                    return defect;
                });

        DefectCreateRequest request =
                new DefectCreateRequest(
                        "Login returns HTTP 500",
                        "Authentication fails unexpectedly.",
                        DefectSeverity.HIGH,
                        "Backend Team"
                );

        DefectResponse response =
                defectService.createDefect(
                        1L,
                        request
                );

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.defectCode())
                .isEqualTo("BUG-0001");
        assertThat(response.status())
                .isEqualTo(DefectStatus.OPEN);
        assertThat(response.severity())
                .isEqualTo(DefectSeverity.HIGH);
    }

    @Test
    void shouldRejectDefectForPassedExecution() {
        failedExecution.setResult(
                TestExecutionResult.PASSED
        );

        when(testExecutionRepository.findById(1L))
                .thenReturn(Optional.of(failedExecution));

        DefectCreateRequest request =
                new DefectCreateRequest(
                        "Invalid defect",
                        "Should not be created.",
                        DefectSeverity.MEDIUM,
                        null
                );

        assertThatThrownBy(() ->
                defectService.createDefect(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Defect can only be created for a FAILED test execution"
                );

        verify(defectRepository, never())
                .save(any(Defect.class));
    }

    @Test
    void shouldRejectSecondDefectForSameExecution() {
        when(testExecutionRepository.findById(1L))
                .thenReturn(Optional.of(failedExecution));

        when(defectRepository.existsByTestExecutionId(1L))
                .thenReturn(true);

        DefectCreateRequest request =
                new DefectCreateRequest(
                        "Duplicate defect",
                        "Duplicate defect description.",
                        DefectSeverity.HIGH,
                        null
                );

        assertThatThrownBy(() ->
                defectService.createDefect(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "A defect already exists for this test execution"
                );

        verify(defectRepository, never())
                .save(any(Defect.class));
    }

    @Test
    void shouldRequireResolutionNotesWhenResolved() {
        Defect defect = Defect.builder()
                .id(1L)
                .defectCode("BUG-0001")
                .title("Login failure")
                .description("Authentication fails.")
                .severity(DefectSeverity.HIGH)
                .status(DefectStatus.OPEN)
                .testExecution(failedExecution)
                .build();

        when(defectRepository.findById(1L))
                .thenReturn(Optional.of(defect));

        DefectUpdateRequest request =
                new DefectUpdateRequest(
                        "Login failure",
                        "Authentication fails.",
                        DefectSeverity.HIGH,
                        DefectStatus.RESOLVED,
                        "Backend Team",
                        ""
                );

        assertThatThrownBy(() ->
                defectService.updateDefect(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Resolution notes are required when defect status is RESOLVED"
                );

        verify(defectRepository, never())
                .save(any(Defect.class));
    }
}