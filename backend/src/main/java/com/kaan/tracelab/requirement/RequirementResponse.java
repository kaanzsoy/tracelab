package com.kaan.tracelab.requirement;

import java.time.LocalDateTime;

public record RequirementResponse(
        Long id,
        String requirementCode,
        String title,
        String description,
        RequirementPriority priority,
        RequirementStatus status,
        Long projectId,
        String projectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static RequirementResponse from(Requirement requirement) {
        return new RequirementResponse(
                requirement.getId(),
                requirement.getRequirementCode(),
                requirement.getTitle(),
                requirement.getDescription(),
                requirement.getPriority(),
                requirement.getStatus(),
                requirement.getProject().getId(),   // proje id ve name bilgileri de donuyor
                requirement.getProject().getName(),
                requirement.getCreatedAt(),
                requirement.getUpdatedAt()
        );
    }
}