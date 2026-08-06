package com.kaan.tracelab.testexecution;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestExecutionRepository
        extends JpaRepository<TestExecution, Long> {

    // belirli bir test run icindeki execution kayitlarini getirir
    List<TestExecution> findByTestRunIdOrderByIdAsc(Long testRunId);
    
    // ayni test case'in ayni test run'a daha once eklenip eklenmedigini kontrol eder
    boolean existsByTestRunIdAndTestCaseId(
            Long testRunId,
            Long testCaseId
    );

    // Spring Data JPA, bu metotlarin SQL sorgularini isimlerinden otomatik uretir!
}