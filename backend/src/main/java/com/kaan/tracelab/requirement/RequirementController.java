package com.kaan.tracelab.requirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @GetMapping("/api/projects/{projectId}/requirements")
    public List<RequirementResponse> getRequirementsByProjectId(
            @PathVariable Long projectId
    ) {
        return requirementService.getRequirementsByProjectId(projectId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TESTER')")
    @PostMapping("/api/projects/{projectId}/requirements")
    @ResponseStatus(HttpStatus.CREATED)
    public RequirementResponse createRequirement(
            @PathVariable Long projectId,
            @Valid @RequestBody RequirementRequest request
    ) {
        return requirementService.createRequirement(projectId, request);
    }

    @GetMapping("/api/requirements/{id}")
    public RequirementResponse getRequirementById(
            @PathVariable Long id
    ) {
        return requirementService.getRequirementById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TESTER')")
    @PutMapping("/api/requirements/{id}")
    public RequirementResponse updateRequirement(
            @PathVariable Long id,
            @Valid @RequestBody RequirementRequest request
    ) {
        return requirementService.updateRequirement(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TESTER')")
    @DeleteMapping("/api/requirements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequirement(
            @PathVariable Long id
    ) {
        requirementService.deleteRequirement(id);
    }
}