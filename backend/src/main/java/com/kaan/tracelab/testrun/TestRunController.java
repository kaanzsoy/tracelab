package com.kaan.tracelab.testrun;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestRunController {

    private final TestRunService testRunService;

    @GetMapping("/api/projects/{projectId}/test-runs")
    public List<TestRunResponse> getTestRunsByProjectId(
            @PathVariable Long projectId
    ) {
        return testRunService.getTestRunsByProjectId(projectId);
    }

    @PostMapping("/api/projects/{projectId}/test-runs")
    @ResponseStatus(HttpStatus.CREATED)
    public TestRunResponse createTestRun(
            @PathVariable Long projectId,
            @Valid @RequestBody TestRunRequest request
    ) {
        return testRunService.createTestRun(projectId, request);
    }

    @GetMapping("/api/test-runs/{id}")
    public TestRunResponse getTestRunById(
            @PathVariable Long id
    ) {
        return testRunService.getTestRunById(id);
    }

    @PutMapping("/api/test-runs/{id}")
    public TestRunResponse updateTestRun(
            @PathVariable Long id,
            @Valid @RequestBody TestRunRequest request
    ) {
        return testRunService.updateTestRun(id, request);
    }

    @DeleteMapping("/api/test-runs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTestRun(
            @PathVariable Long id
    ) {
        testRunService.deleteTestRun(id);
    }
}