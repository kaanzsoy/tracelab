package com.kaan.tracelab.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// burada "record" kullaniyoruz --> sadece veri tasimak icin ozel bir class turu
// DTO --> data transfer object

// java otomatik olarak "constructor, getter benzeri accessor, equals, hashCode, toString" uretir
// record --> immutable

public record ProjectRequest(
        @NotBlank(message = "Project name cannot be blank")
        @Size(max = 150, message = "Project name must be at most 150 characters")
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        ProjectStatus status
) {
}