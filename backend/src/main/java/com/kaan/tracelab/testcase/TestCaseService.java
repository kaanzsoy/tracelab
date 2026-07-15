package com.kaan.tracelab.testcase;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.requirement.Requirement;
import com.kaan.tracelab.requirement.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final RequirementRepository requirementRepository;

    @Transactional(readOnly = true)
    public List<TestCaseResponse> getTestCasesByRequirementId(Long requirementId) {
        ensureRequirementExists(requirementId);

        return testCaseRepository.findByRequirementIdOrderByIdAsc(requirementId)
                .stream()
                .map(TestCaseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TestCaseResponse getTestCaseById(Long id) {
        TestCase testCase = findTestCaseOrThrow(id);
        return TestCaseResponse.from(testCase);
    }

    public TestCaseResponse createTestCase(
            Long requirementId,
            TestCaseRequest request
    ) {
        Requirement requirement = findRequirementOrThrow(requirementId);

        TestCase testCase = TestCase.builder()
                .title(request.title())
                .preconditions(request.preconditions())
                .testSteps(request.testSteps())
                .expectedResult(request.expectedResult())
                .type(request.type())
                .priority(request.priority())
                .status(request.status())
                .requirement(requirement)
                .build();

        TestCase savedTestCase = testCaseRepository.save(testCase);

        savedTestCase.setTestCaseCode(
                String.format("TC-%04d", savedTestCase.getId())
        );

        TestCase codedTestCase = testCaseRepository.save(savedTestCase);

        return TestCaseResponse.from(codedTestCase);
    }

    // Requirement degistirilmiyor, daha sonra eklenebilir, simdilik tasarim bu sekilde

    public TestCaseResponse updateTestCase(
            Long id,
            TestCaseRequest request
    ) {
        TestCase testCase = findTestCaseOrThrow(id);

        testCase.setTitle(request.title());
        testCase.setPreconditions(request.preconditions());
        testCase.setTestSteps(request.testSteps());
        testCase.setExpectedResult(request.expectedResult());
        testCase.setType(request.type());
        testCase.setPriority(request.priority());
        testCase.setStatus(request.status());

        TestCase updatedTestCase = testCaseRepository.save(testCase);

        return TestCaseResponse.from(updatedTestCase);
    }

    public void deleteTestCase(Long id) {
        TestCase testCase = findTestCaseOrThrow(id);
        testCaseRepository.delete(testCase);
    }

    private TestCase findTestCaseOrThrow(Long id) {
        return testCaseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Test case not found with id: " + id
                        )
                );
    }

    private Requirement findRequirementOrThrow(Long requirementId) {
        return requirementRepository.findById(requirementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Requirement not found with id: " + requirementId
                        )
                );
    }

    private void ensureRequirementExists(Long requirementId) {
        if (!requirementRepository.existsById(requirementId)) {
            throw new ResourceNotFoundException(
                    "Requirement not found with id: " + requirementId
            );
        }
    }
}