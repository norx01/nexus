package com.nexus.nexus.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "registro_entrada_personal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEntradaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_registro;

    private Integer id_personal;

    private LocalDate fecha;

    private LocalTime hora_entrada;

    private LocalTime hora_salida;

    private String estado; // "Dentro" / "Retirado"
}
