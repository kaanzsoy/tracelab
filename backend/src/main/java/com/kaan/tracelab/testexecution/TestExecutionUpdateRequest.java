package com.kaan.tracelab.testexecution;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TestExecutionUpdateRequest(

        @NotNull(message = "Test execution result is required")
        TestExecutionResult result,

        @Size(
                max = 3000,
                message = "Actual result must be at most 3000 characters"
        )
        String actualResult,

        @Size(
                max = 2000,
                message = "Notes must be at most 2000 characters"
        )
        String notes,

        LocalDateTime executedAt
) {
}