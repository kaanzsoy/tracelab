package com.kaan.tracelab.testexecution;

import jakarta.validation.constraints.NotNull;

public record TestExecutionCreateRequest(

        @NotNull(message = "Test case ID is required")
        Long testCaseId
) {
}