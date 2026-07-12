package com.kaan.tracelab.requirement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {

    List<Requirement> findByProjectIdOrderByIdAsc(Long projectId);
    // spring data JPA, metodun isminden sorguyu uretir.
    // Mantiksal SQL karsiligi:
   /* 
    SELECT *
    FROM requirements
    WHERE project_id = ?
    ORDER BY id ASC;
   */
}