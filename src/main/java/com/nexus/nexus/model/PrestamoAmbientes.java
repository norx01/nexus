package com.nexus.nexus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "prestamo_ambientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoAmbientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_prestamo;

    @NotNull(message = "El personal es obligatorio")
    private Integer id_personal;

    @NotNull(message = "El ambiente es obligatorio")
    private Integer id_ambiente;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de entrada es obligatoria")
    private LocalTime hora_entrada;

    @NotNull(message = "La hora de salida es obligatoria")
    private LocalTime hora_salida;
}
