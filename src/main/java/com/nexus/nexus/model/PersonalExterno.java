package com.nexus.nexus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "personal_externo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalExterno
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_personal_externo;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipo_documento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    private String empresa;

    @NotBlank(message = "El motivo de la visita es obligatorio")
    private String motivo_visita;

    @NotNull(message = "La fecha de la visita es obligatoria")
    private LocalDate fecha_visita;

    @NotNull(message = "La hora de la entrada es obligatoria")
    private LocalTime hora_ingreso;

    private LocalTime hora_salida;

    private Long tiempo_estancia;

    @NotBlank(message = "El estado de la visita es obligatorio")
    private String estado;

    private LocalDate fecha_creacion;

    @PrePersist
    @PreUpdate
    public void calcularTiempoEstancia()
    {
        if (hora_ingreso != null && hora_salida != null)
        {
            this.tiempo_estancia = Duration.between(hora_ingreso, hora_salida).toMinutes();
        }
        else
        {
            this.tiempo_estancia = 0L;
        }
    }









}
