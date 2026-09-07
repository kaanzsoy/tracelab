package com.kaan.tracelab.testrun;

import com.kaan.tracelab.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "test_runs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_test_run_code",
                        columnNames = "run_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_code", unique = true)
    private String runCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestRunStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_test_run_project")
    )
    private Project project;
    // bir proje, bircok test run icerebilir
    // her test run, yalizca bir projeye baglidir

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.environment == null) {
            this.environment = TestEnvironment.TEST;
        }

        if (this.status == null) {
            this.status = TestRunStatus.PLANNED;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}