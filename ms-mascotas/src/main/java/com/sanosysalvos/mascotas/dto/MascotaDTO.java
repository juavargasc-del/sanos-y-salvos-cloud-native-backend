package com.sanosysalvos.mascotas.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String raza;
    private String color;
    private Integer edad;
    private String dimension;
    private String estado;
}