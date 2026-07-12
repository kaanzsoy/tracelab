package com.kaan.tracelab.requirement;

import com.kaan.tracelab.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "requirements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_requirement_code",
                        columnNames = "requirement_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requirement_code", unique = true)
    private String requirementCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementStatus status;

    // bircok requirement ayni project'e bagli olabilir
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",    // foreign key kolonunun adi
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_requirement_project")
    )
    private Project project;    // aslinda bir nesne referansi
    // Requirement icinde su yok yani: private Long projectId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.priority == null) {
            this.priority = RequirementPriority.MEDIUM;
        }

        if (this.status == null) {
            this.status = RequirementStatus.DRAFT;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}