package com.nexus.nexus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "ambientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ambientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ambiente;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La sede es obligatoria")
    private String sede;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private String observaciones;

    private Integer id_personal_activo; // NULL = disponible
}
