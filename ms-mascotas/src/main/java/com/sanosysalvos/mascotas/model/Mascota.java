package com.sanosysalvos.mascotas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(columnDefinition = "LONGTEXT")
    private String fotoBase64;

    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    @Column(nullable = false)
    private String estado; // PERDIDA o ENCONTRADA
}
