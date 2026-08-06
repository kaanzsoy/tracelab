package com.kaan.tracelab.testcase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByRequirementIdOrderByIdAsc(
            Long requirementId
    );

    long countByRequirementProjectId(Long projectId);

    // belirli bir requirement'a bagli test case'leri id sirasina gore getir
    // JPA, metot isminden sorguyu otomatik uretir
    /*
        SELECT *
        FROM test_cases
        WHERE requirement_id = ?
        ORDER BY id ASC;
    */
}