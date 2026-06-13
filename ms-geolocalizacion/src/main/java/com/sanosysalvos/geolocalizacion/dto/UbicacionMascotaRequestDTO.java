package com.sanosysalvos.geolocalizacion.dto;

import jakarta.validation.constraints.NotNull;
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
public class UbicacionMascotaRequestDTO {

    @NotNull
    private Long mascotaId;

    @NotNull
    private Double latitud;

    @NotNull
    private Double longitud;

    private LocalDateTime fechaRegistro;
}
