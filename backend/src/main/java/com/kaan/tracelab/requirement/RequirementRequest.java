package com.kaan.tracelab.requirement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequirementRequest(

    // projectId, URL'den aliniyor.
    // POST /api/projects/1/requirements

        @NotBlank(message = "Requirement title cannot be blank")
        @Size(
                max = 200,
                message = "Requirement title must be at most 200 characters"
        )
        String title,

        @Size(
                max = 2000,
                message = "Requirement description must be at most 2000 characters"
        )
        String description,

        @NotNull(message = "Requirement priority is required")
        RequirementPriority priority,

        @NotNull(message = "Requirement status is required")
        RequirementStatus status
) {
}