package com.kaan.tracelab.testrun;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TestRunRequest(

        @NotBlank(message = "Test run name cannot be blank")
        @Size(
                max = 200,
                message = "Test run name must be at most 200 characters"
        )
        String name,

        @Size(
                max = 2000,
                message = "Description must be at most 2000 characters"
        )
        String description,

        @NotNull(message = "Test environment is required")
        TestEnvironment environment,

        @NotNull(message = "Test run status is required")
        TestRunStatus status,

        LocalDateTime startedAt,

        LocalDateTime completedAt
) {
}