package com.sanosysalvos.coincidencias.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coincidencia {

    private Long idMascotaPerdida;
    private Long idMascotaEncontrada;

    private String tipo;
    private String raza;
    private String color;

    private String descripcion;
}