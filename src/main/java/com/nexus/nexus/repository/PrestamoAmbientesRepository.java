package com.nexus.nexus.repository;

import com.nexus.nexus.model.PrestamoAmbientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrestamoAmbientesRepository extends JpaRepository<PrestamoAmbientes, Long> {

    // Préstamo activo de un instructor
    @Query("SELECT p FROM PrestamoAmbientes p WHERE p.id_personal = :idPersonal AND p.estado = 'Activo'")
    Optional<PrestamoAmbientes> findPrestamoActivo(@Param("idPersonal") Integer idPersonal);

    // Todos los préstamos activos
    List<PrestamoAmbientes> findByEstado(String estado);
}
