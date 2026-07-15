package com.kaan.tracelab.testcase;

import com.kaan.tracelab.requirement.Requirement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "test_cases",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_test_case_code",
                        columnNames = "test_case_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //@Column(name = "test_case_code", nullable = false, unique = true)
    @Column(name = "test_case_code", unique = true) // nullable kaldirdik, cunku entity ilk kez kaydedilirken testCaseCode henuz belli olmuyor, o kodu veritabaninin urettigi ID uzerinden olusturuyoruz
    // ID 1 → TC-0001
    private String testCaseCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String preconditions;

    @Column(name = "test_steps", nullable = false, length = 5000)
    private String testSteps;

    @Column(name = "expected_result", nullable = false, length = 2000)
    private String expectedResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCaseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requirement_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_test_case_requirement")
    )
    private Requirement requirement;
    // bir requirement'a bircok test case baglanabilir, ancak her test case yalnizca bir requirement'a bagli

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.type == null) {
            this.type = TestCaseType.FUNCTIONAL;
        }

        if (this.priority == null) {
            this.priority = TestCasePriority.MEDIUM;
        }

        if (this.status == null) {
            this.status = TestCaseStatus.DRAFT;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}