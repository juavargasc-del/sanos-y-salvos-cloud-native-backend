package com.sanosysalvos.bff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private String rol;
}