package com.kaan.tracelab.testrun;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRunRepository extends JpaRepository<TestRun, Long> {

    List<TestRun> findByProjectIdOrderByIdAsc(Long projectId);

    long countByProjectId(Long projectId);
}