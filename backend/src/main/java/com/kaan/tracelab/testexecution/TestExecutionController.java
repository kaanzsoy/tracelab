package com.kaan.tracelab.testexecution;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestExecutionController {

    private final TestExecutionService testExecutionService;

    @GetMapping("/api/test-runs/{testRunId}/executions")
    public List<TestExecutionResponse> getExecutionsByTestRunId(
            @PathVariable Long testRunId
    ) {
        return testExecutionService
                .getExecutionsByTestRunId(testRunId);
    }

    @PostMapping("/api/test-runs/{testRunId}/executions")
    @ResponseStatus(HttpStatus.CREATED)
    public TestExecutionResponse createExecution(
            @PathVariable Long testRunId,
            @Valid @RequestBody
            TestExecutionCreateRequest request
    ) {
        return testExecutionService
                .createExecution(testRunId, request);
    }

    @GetMapping("/api/test-executions/{id}")
    public TestExecutionResponse getExecutionById(
            @PathVariable Long id
    ) {
        return testExecutionService.getExecutionById(id);
    }

    @PutMapping("/api/test-executions/{id}")
    public TestExecutionResponse updateExecution(
            @PathVariable Long id,
            @Valid @RequestBody
            TestExecutionUpdateRequest request
    ) {
        return testExecutionService
                .updateExecution(id, request);
    }

    @DeleteMapping("/api/test-executions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExecution(
            @PathVariable Long id
    ) {
        testExecutionService.deleteExecution(id);
    }
}