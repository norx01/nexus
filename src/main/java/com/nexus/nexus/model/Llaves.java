package com.nexus.nexus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "llaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Llaves {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_llave;

    private Long id_ambiente;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
