package com.kaan.tracelab.project;

import java.time.LocalDateTime;

// disariya return edecegimiz veri formati
public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}