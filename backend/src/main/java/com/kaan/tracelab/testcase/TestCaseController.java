package com.kaan.tracelab.testcase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    @GetMapping("/api/requirements/{requirementId}/test-cases")
    public List<TestCaseResponse> getTestCasesByRequirementId(
            @PathVariable Long requirementId
    ) {
        return testCaseService.getTestCasesByRequirementId(requirementId);
    }

    @PostMapping("/api/requirements/{requirementId}/test-cases")
    @ResponseStatus(HttpStatus.CREATED)
    public TestCaseResponse createTestCase(
            @PathVariable Long requirementId,
            @Valid @RequestBody TestCaseRequest request
    ) {
        return testCaseService.createTestCase(requirementId, request);
    }

    @GetMapping("/api/test-cases/{id}")
    public TestCaseResponse getTestCaseById(
            @PathVariable Long id
    ) {
        return testCaseService.getTestCaseById(id);
    }

    @PutMapping("/api/test-cases/{id}")
    public TestCaseResponse updateTestCase(
            @PathVariable Long id,
            @Valid @RequestBody TestCaseRequest request
    ) {
        return testCaseService.updateTestCase(id, request);
    }

    @DeleteMapping("/api/test-cases/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTestCase(
            @PathVariable Long id
    ) {
        testCaseService.deleteTestCase(id);
    }
}