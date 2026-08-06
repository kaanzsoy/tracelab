package com.kaan.tracelab.defect;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DefectUpdateRequest(

        @NotBlank(message = "Defect title cannot be blank")
        @Size(
                max = 200,
                message = "Defect title must be at most 200 characters"
        )
        String title,

        @NotBlank(message = "Defect description cannot be blank")
        @Size(
                max = 3000,
                message = "Defect description must be at most 3000 characters"
        )
        String description,

        @NotNull(message = "Defect severity is required")
        DefectSeverity severity,

        @NotNull(message = "Defect status is required")
        DefectStatus status,

        @Size(
                max = 150,
                message = "Assigned user must be at most 150 characters"
        )
        String assignedTo,

        @Size(
                max = 3000,
                message = "Resolution notes must be at most 3000 characters"
        )
        String resolutionNotes
) {
}