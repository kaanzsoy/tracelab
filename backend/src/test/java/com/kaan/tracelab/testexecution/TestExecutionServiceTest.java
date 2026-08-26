package com.kaan.tracelab.testexecution;

import com.kaan.tracelab.project.Project;
import com.kaan.tracelab.requirement.Requirement;
import com.kaan.tracelab.testcase.TestCase;
import com.kaan.tracelab.testcase.TestCaseRepository;
import com.kaan.tracelab.testrun.TestRun;
import com.kaan.tracelab.testrun.TestRunRepository;
import com.kaan.tracelab.testrun.TestRunStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestExecutionServiceTest {

    @Mock
    private TestExecutionRepository testExecutionRepository;
    // bu testlerde gercek PostgreSQL KULLANMIYORUZ
    // repository'leri Mockito ile sahte nesne haline getiriyoruz

    @Mock
    private TestRunRepository testRunRepository;

    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private TestExecutionService testExecutionService;
    // gercek bir servis

    private Project project;
    private TestRun testRun;
    private TestCase testCase;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("TraceLab Core Backend")
                .build();

        testRun = TestRun.builder()
                .id(1L)
                .runCode("RUN-0001")
                .name("Regression Run")
                .status(TestRunStatus.IN_PROGRESS)
                .project(project)
                .build();

        Requirement requirement = Requirement.builder()
                .id(1L)
                .requirementCode("REQ-0001")
                .title("User Authentication")
                .project(project)
                .build();

        testCase = TestCase.builder()
                .id(1L)
                .testCaseCode("TC-0001")
                .title("Login with valid credentials")
                .requirement(requirement)
                .build();
    }

    @Test
    void shouldCreateExecutionSuccessfully() {
        TestExecutionCreateRequest request =
                new TestExecutionCreateRequest(1L);

        // repository'ye ID 1 soruldugunda veritabanina GITME, hazirladigimiz testRun objesini return et
        when(testRunRepository.findById(1L))
                .thenReturn(Optional.of(testRun));

        when(testCaseRepository.findById(1L))
                .thenReturn(Optional.of(testCase));

        when(
                testExecutionRepository
                        .existsByTestRunIdAndTestCaseId(1L, 1L)
        ).thenReturn(false);

        when(testExecutionRepository.save(any(TestExecution.class)))
                .thenAnswer(invocation -> {
                    TestExecution execution =
                            invocation.getArgument(0);

                    if (execution.getId() == null) {
                        execution.setId(1L);
                    }

                    return execution;
                });

        TestExecutionResponse response =
                testExecutionService.createExecution(
                        1L,
                        request
                );

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.executionCode())
                .isEqualTo("EXEC-0001");
        assertThat(response.result())
                .isEqualTo(TestExecutionResult.NOT_RUN);

        verify(testExecutionRepository, times(2))
                .save(any(TestExecution.class));
    }

    @Test
    void shouldRejectDuplicateTestCaseInSameRun() {
        TestExecutionCreateRequest request =
                new TestExecutionCreateRequest(1L);

        when(testRunRepository.findById(1L))
                .thenReturn(Optional.of(testRun));

        when(testCaseRepository.findById(1L))
                .thenReturn(Optional.of(testCase));

        when(
                testExecutionRepository
                        .existsByTestRunIdAndTestCaseId(1L, 1L)
        ).thenReturn(true);

        assertThatThrownBy(() ->
                testExecutionService.createExecution(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Test case is already included in this test run"
                );

        verify(testExecutionRepository, never())
                .save(any(TestExecution.class));
    }

    @Test
    void shouldRejectTestCaseFromDifferentProject() {
        Project anotherProject = Project.builder()
                .id(2L)
                .name("Another Project")
                .build();

        Requirement anotherRequirement =
                Requirement.builder()
                        .id(2L)
                        .requirementCode("REQ-0002")
                        .title("Another Requirement")
                        .project(anotherProject)
                        .build();

        TestCase anotherTestCase = TestCase.builder()
                .id(2L)
                .testCaseCode("TC-0002")
                .title("Another Test")
                .requirement(anotherRequirement)
                .build();

        TestExecutionCreateRequest request =
                new TestExecutionCreateRequest(2L);

        when(testRunRepository.findById(1L))
                .thenReturn(Optional.of(testRun));

        when(testCaseRepository.findById(2L))
                .thenReturn(Optional.of(anotherTestCase));

        assertThatThrownBy(() ->
                testExecutionService.createExecution(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Test case and test run must belong to the same project"
                );

        verify(testExecutionRepository, never())
                .save(any(TestExecution.class));
    }

    @Test
    void shouldRequireActualResultWhenExecutionFails() {
        TestExecution execution = TestExecution.builder()
                .id(1L)
                .executionCode("EXEC-0001")
                .testRun(testRun)
                .testCase(testCase)
                .result(TestExecutionResult.NOT_RUN)
                .build();

        when(testExecutionRepository.findById(1L))
                .thenReturn(Optional.of(execution));

        TestExecutionUpdateRequest request =
                new TestExecutionUpdateRequest(
                        TestExecutionResult.FAILED,
                        "",
                        "Authentication failed.",
                        LocalDateTime.now()
                );

        assertThatThrownBy(() ->
                testExecutionService.updateExecution(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Actual result is required when execution result is FAILED"
                );

        verify(testExecutionRepository, never())
                .save(any(TestExecution.class));
    }

    @Test
    void shouldRejectModificationOfCompletedRun() {
        testRun.setStatus(TestRunStatus.COMPLETED);

        TestExecution execution = TestExecution.builder()
                .id(1L)
                .executionCode("EXEC-0001")
                .testRun(testRun)
                .testCase(testCase)
                .result(TestExecutionResult.NOT_RUN)
                .build();

        when(testExecutionRepository.findById(1L))
                .thenReturn(Optional.of(execution));

        TestExecutionUpdateRequest request =
                new TestExecutionUpdateRequest(
                        TestExecutionResult.PASSED,
                        "Expected result observed.",
                        null,
                        LocalDateTime.now()
                );

        assertThatThrownBy(() ->
                testExecutionService.updateExecution(
                        1L,
                        request
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Completed test runs cannot be modified"
                );

        verify(testExecutionRepository, never())
                .save(any(TestExecution.class));
    }
}