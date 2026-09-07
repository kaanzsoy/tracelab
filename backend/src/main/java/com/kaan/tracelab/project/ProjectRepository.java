package com.kaan.tracelab.project;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository'de zaten findAll(), findById(), save(), deleteById(), existsById() gibi komutlar var
// Yani, SQL yazmadan temel CRUD islemlerini yapabiliriz

public interface ProjectRepository extends JpaRepository<Project, Long> {
}