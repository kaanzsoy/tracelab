package com.kaan.tracelab.testexecution;

import com.kaan.tracelab.testcase.TestCase;
import com.kaan.tracelab.testrun.TestRun;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "test_executions",
        uniqueConstraints = {
                @UniqueConstraint(
                    // ayni test case'in ayni run'a iki kez eklenmesini engeller
                    /*
                    Gecerli:
                    RUN-0001 + TC-0001
                    RUN-0001 + TC-0002
                    RUN-0002 + TC-0001

                    Gecersiz!:
                    RUN-0001 + TC-0001
                    RUN-0001 + TC-0001
                     */
                        name = "uk_test_execution_run_case",
                        columnNames = {
                                "test_run_id",
                                "test_case_id"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_test_execution_code",
                        columnNames = "execution_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_code", unique = true)
    private String executionCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "test_run_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_test_execution_test_run"
            )
    )
    private TestRun testRun;
    // test_run_id  → test_runs.id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "test_case_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_test_execution_test_case"
            )
    )
    private TestCase testCase;
    // test_case_id → test_cases.id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestExecutionResult result;

    @Column(name = "actual_result", length = 3000)
    private String actualResult;

    @Column(length = 2000)
    private String notes;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.result == null) {
            this.result = TestExecutionResult.NOT_RUN;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}