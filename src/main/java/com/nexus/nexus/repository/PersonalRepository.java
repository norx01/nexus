package com.nexus.nexus.repository;

import com.nexus.nexus.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalRepository extends JpaRepository<Personal, Integer> {
    Optional<Personal> findByDocumento(String documento);
}
