package com.sanosysalvos.geolocalizacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionMascotaDTO {

    private Long id;
    private Long mascotaId;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaRegistro;
}
