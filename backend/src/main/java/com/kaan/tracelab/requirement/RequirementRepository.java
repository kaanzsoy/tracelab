package com.kaan.tracelab.requirement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    long countByProjectId(Long projectId);

    // buradaki @Query, SQL degil JPQL kullaniyor
    // tablo isimleri yerine Java entity ve alan isimleri yazilir
    @Query("""
            SELECT COUNT(DISTINCT tc.requirement.id)
            FROM TestCase tc
            WHERE tc.requirement.project.id = :projectId
            """)
    long countCoveredRequirementsByProjectId(
            @Param("projectId") Long projectId
    );

    // countCoveredRequirementsByProjectId(projectId) --> en az bir test case'e sahip requirement sayisi

}