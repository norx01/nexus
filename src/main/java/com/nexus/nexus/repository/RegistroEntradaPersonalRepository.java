package com.nexus.nexus.repository;

import com.nexus.nexus.model.RegistroEntradaPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroEntradaPersonalRepository extends JpaRepository<RegistroEntradaPersonal, Long> {

    // Busca un registro activo (dentro) de hoy para un instructor
    @Query("SELECT r FROM RegistroEntradaPersonal r WHERE r.id_personal = :idPersonal AND r.fecha = :fecha AND r.estado = 'Dentro'")
    Optional<RegistroEntradaPersonal> findRegistroActivoHoy(@Param("idPersonal") Integer idPersonal, @Param("fecha") LocalDate fecha);

    // Todos los registros de una fecha ordenados por hora de entrada desc
    @Query("SELECT r FROM RegistroEntradaPersonal r WHERE r.fecha = :fecha ORDER BY r.hora_entrada DESC")
    List<RegistroEntradaPersonal> findByFecha(@Param("fecha") LocalDate fecha);

    // Registros activos (dentro)
    @Query("SELECT r FROM RegistroEntradaPersonal r WHERE r.estado = :estado ORDER BY r.fecha DESC, r.hora_entrada DESC")
    List<RegistroEntradaPersonal> findByEstado(@Param("estado") String estado);
}
