package com.sanosysalvos.coincidencias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaEventoDTO {

    private UUID eventId;
    private String tipoEvento;
    private Long mascotaId;
    private String nombre;
    private String estado;
    private Long usuarioId;
    private Instant timestamp;
}
