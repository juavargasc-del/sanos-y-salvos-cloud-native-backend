package com.sanosysalvos.mascotas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String raza;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false)
    private String dimension;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false)
    private String estado; // PERDIDA o ENCONTRADA
}
