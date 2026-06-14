package com.sanosysalvos.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private String rol;
}