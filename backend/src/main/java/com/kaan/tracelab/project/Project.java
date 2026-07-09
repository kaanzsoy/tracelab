package com.kaan.tracelab.project;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity // bu class veritabani olarak kullanilacak
@Table(name = "projects")

// Lombok annotation'lari
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Project {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id otomatik atansin
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)    // "ACTIVE" gibi string yazilsin
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist // ilk kayit olusturulurken calisir
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = ProjectStatus.ACTIVE;
        }
    }

    @PreUpdate  // update sirasinda calisir
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}