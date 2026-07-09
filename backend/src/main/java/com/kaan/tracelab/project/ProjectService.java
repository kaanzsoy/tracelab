package com.kaan.tracelab.project;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = findProjectOrThrow(id);
        return ProjectResponse.from(project);
    }

    public ProjectResponse createProject(ProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status() != null ? request.status() : ProjectStatus.ACTIVE)
                .build();

        Project savedProject = projectRepository.save(project);

        return ProjectResponse.from(savedProject);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = findProjectOrThrow(id);

        project.setName(request.name());
        project.setDescription(request.description());

        if (request.status() != null) {
            project.setStatus(request.status());
        }

        Project updatedProject = projectRepository.save(project);

        return ProjectResponse.from(updatedProject);
    }

    public void deleteProject(Long id) {
        Project project = findProjectOrThrow(id);
        projectRepository.delete(project);
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
}