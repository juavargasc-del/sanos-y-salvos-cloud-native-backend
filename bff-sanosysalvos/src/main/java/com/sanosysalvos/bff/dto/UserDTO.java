package com.sanosysalvos.bff.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String nombre;

    private String email;

    private String password;

    private String rol;
}