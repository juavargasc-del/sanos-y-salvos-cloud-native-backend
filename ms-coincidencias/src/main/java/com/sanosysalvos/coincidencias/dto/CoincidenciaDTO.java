package com.sanosysalvos.coincidencias.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoincidenciaDTO {

    private Long idMascotaPerdida;
    private Long idMascotaEncontrada;

    private String nombreMascotaPerdida;
    private String nombreMascotaEncontrada;

    private String tipo;
    private String raza;
    private String color;

    private String descripcion;
}