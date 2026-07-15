package com.kaan.tracelab.testcase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TestCaseRequest(

        @NotBlank(message = "Test case title cannot be blank")
        @Size(
                max = 200,
                message = "Test case title must be at most 200 characters"
        )
        String title,

        @Size(
                max = 2000,
                message = "Preconditions must be at most 2000 characters"
        )
        String preconditions,

        @NotBlank(message = "Test steps cannot be blank")
        @Size(
                max = 5000,
                message = "Test steps must be at most 5000 characters"
        )
        String testSteps,

        @NotBlank(message = "Expected result cannot be blank")
        @Size(
                max = 2000,
                message = "Expected result must be at most 2000 characters"
        )
        String expectedResult,

        @NotNull(message = "Test case type is required")
        TestCaseType type,

        @NotNull(message = "Test case priority is required")
        TestCasePriority priority,

        @NotNull(message = "Test case status is required")
        TestCaseStatus status
) {
}