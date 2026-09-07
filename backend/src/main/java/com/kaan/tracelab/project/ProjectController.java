package com.kaan.tracelab.project;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // JSON API controller
@RequestMapping("/api/projects")    // tum endpointler bu path altinda
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // get metotlarina PreAuthorize eklemiyoruz
    // Zaten .anyRequest().authenticated() sayesinde login olmak gerekiyor
    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        // Valid --> DTO validation calisir
        // RequestBody --> JSON body'yi Java objesine cevirir
        return projectService.createProject(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,  // PathVariable --> URL'deki id degerini alir
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.updateProject(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}