package com.sanosysalvos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private String rol;
}