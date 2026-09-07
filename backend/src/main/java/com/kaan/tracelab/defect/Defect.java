package com.kaan.tracelab.defect;

import com.kaan.tracelab.testexecution.TestExecution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "defects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_defect_code",
                        columnNames = "defect_code"
                ),
                @UniqueConstraint(
                    // ayni execution icin iki defect acilamaz
                        name = "uk_defect_test_execution",
                        columnNames = "test_execution_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "defect_code", unique = true)
    private String defectCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefectSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefectStatus status;

    @Column(name = "assigned_to", length = 150)
    private String assignedTo;

    @Column(name = "resolution_notes", length = 3000)
    private String resolutionNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            // defect'i execution'a bagliyor
            // PostgreSQL'da defects.test_execution_id kolonu olusur, test_execution.id'ye baglanir
            name = "test_execution_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_defect_test_execution"
            )
    )
    private TestExecution testExecution;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.severity == null) {
            this.severity = DefectSeverity.MEDIUM;
        }

        if (this.status == null) {
            this.status = DefectStatus.OPEN;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}