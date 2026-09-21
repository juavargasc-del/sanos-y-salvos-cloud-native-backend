package com.sanosysalvos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CurrentUserResponseDTO {

    private final boolean linked;
    private final Long userId;
    private final String nombre;
    private final String email;
    private final String rol;
    private final String message;
}
