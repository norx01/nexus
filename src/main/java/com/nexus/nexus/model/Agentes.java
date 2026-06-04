package com.nexus.nexus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "agentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agentes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_agente;

    @NotBlank(message = "El nombre del agente es obligatorio")
    private String nombre_del_agente;

    @NotBlank(message = "El correo es obligatorio")
    private String correo;
}
