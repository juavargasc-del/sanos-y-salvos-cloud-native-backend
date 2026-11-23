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
public class MascotaAlertaDTO {

    private UUID alertaId;
    private UUID eventoOrigenId;
    private Long mascotaId;
    private Long usuarioId;
    private String tipoAlerta;
    private String mensaje;
    private Instant timestamp;
}
