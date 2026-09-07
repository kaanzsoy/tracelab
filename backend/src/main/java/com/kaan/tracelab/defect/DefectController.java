package com.kaan.tracelab.defect;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TESTER')")
    @PostMapping(
            "/api/test-executions/{executionId}/defects"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public DefectResponse createDefect(
            @PathVariable Long executionId,
            @Valid @RequestBody DefectCreateRequest request
    ) {
        return defectService.createDefect(
                executionId,
                request
        );
    }

    @GetMapping("/api/projects/{projectId}/defects")
    public List<DefectResponse> getDefectsByProjectId(
            @PathVariable Long projectId
    ) {
        return defectService.getDefectsByProjectId(projectId);
    }

    @GetMapping("/api/defects/{id}")
    public DefectResponse getDefectById(
            @PathVariable Long id
    ) {
        return defectService.getDefectById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TESTER', 'DEVELOPER')")
    @PutMapping("/api/defects/{id}")
    public DefectResponse updateDefect(
            @PathVariable Long id,
            @Valid @RequestBody DefectUpdateRequest request
    ) {
        return defectService.updateDefect(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/defects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDefect(
            @PathVariable Long id
    ) {
        defectService.deleteDefect(id);
    }
}