package com.kaan.tracelab.requirement;

import com.kaan.tracelab.common.exception.ResourceNotFoundException;
import com.kaan.tracelab.project.Project;
import com.kaan.tracelab.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<RequirementResponse> getRequirementsByProjectId(Long projectId) {
        ensureProjectExists(projectId);

        return requirementRepository.findByProjectIdOrderByIdAsc(projectId)
                .stream()
                .map(RequirementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true) // buradaki, service icindeki islemleri bir veritabani transaction'i icinde yurutuyor
    public RequirementResponse getRequirementById(Long id) {
        Requirement requirement = findRequirementOrThrow(id);
        return RequirementResponse.from(requirement);
    }

    /*  TRANSACTIONAL icin ornek;
        Örneğin create sırasında:
            1- Requirement kaydedilir
            2- ID alinir
            3- Requirement kodu uretilir
            4- Tekrar guncellenir

        Bu islemlerden biri basarisiz olursa transaction geri alinabilir
     */

    public RequirementResponse createRequirement(
            Long projectId,
            RequirementRequest request
    ) {
        Project project = findProjectOrThrow(projectId);

        Requirement requirement = Requirement.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .status(request.status())
                .project(project)
                .build();

        // kayit veritabanina eklenir ve id olusur
        Requirement savedRequirement = requirementRepository.save(requirement);

        // bu id'den kod uretilir, sistem otomatik yaziyor
        /*
            ID 1   → REQ-0001
            ID 12  → REQ-0012
            ID 148 → REQ-0148
        */
        savedRequirement.setRequirementCode(
                String.format("REQ-%04d", savedRequirement.getId())
        );

        Requirement codedRequirement =
                requirementRepository.save(savedRequirement);

        return RequirementResponse.from(codedRequirement);
    }

    public RequirementResponse updateRequirement(
            Long id,
            RequirementRequest request
    ) {
        Requirement requirement = findRequirementOrThrow(id);

        requirement.setTitle(request.title());
        requirement.setDescription(request.description());
        requirement.setPriority(request.priority());
        requirement.setStatus(request.status());

        Requirement updatedRequirement =
                requirementRepository.save(requirement);

        return RequirementResponse.from(updatedRequirement);
    }

    public void deleteRequirement(Long id) {
        Requirement requirement = findRequirementOrThrow(id);
        requirementRepository.delete(requirement);
    }

    private Requirement findRequirementOrThrow(Long id) {
        return requirementRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Requirement not found with id: " + id
                        )
                );
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );
    }

    private void ensureProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }
    }
}