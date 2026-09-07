package com.kaan.tracelab.defect;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DefectRepository
        extends JpaRepository<Defect, Long> {

    boolean existsByTestExecutionId(Long testExecutionId);

    List<Defect>
    findByTestExecutionTestRunProjectIdOrderByIdAsc(
            Long projectId
    );

    long countByTestExecutionTestRunProjectId(
            Long projectId
    );

    long countByTestExecutionTestRunProjectIdAndStatusIn(
            Long projectId,
            Collection<DefectStatus> statuses
    );

    long countByTestExecutionTestRunProjectIdAndSeverity(
            Long projectId,
            DefectSeverity severity
    );
}